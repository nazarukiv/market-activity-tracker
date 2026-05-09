package com.marketmonitor.analyzer;

import com.marketmonitor.model.Trade;
import com.marketmonitor.model.WhaleTrade;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WhaleAnalyzer {

    private static final double WHALE_TRADE_THRESHOLD_USD = 10000.0;
    private static final int SUDDEN_ACTIVITY_MIN_TRADES = 3;
    private static final Duration SUDDEN_ACTIVITY_WINDOW = Duration.ofMinutes(10);

    public List<WhaleTrade> findWhaleTrades(List<Trade> trades) {
        if (trades == null || trades.isEmpty()) {
            return List.of();
        }

        return trades.stream()
                .filter(trade -> tradeAmountUsd(trade) >= WHALE_TRADE_THRESHOLD_USD)
                .map(this::toWhaleTrade)
                .toList();
    }

    public Map<String, List<WhaleTrade>> detectSuddenActivity(List<WhaleTrade> whaleTrades) {
        if (whaleTrades == null || whaleTrades.isEmpty()) {
            return Map.of();
        }

        return whaleTrades.stream()
                .filter(trade -> trade.getWallet() != null && !trade.getWallet().isBlank())
                .collect(Collectors.groupingBy(WhaleTrade::getWallet))
                .entrySet()
                .stream()
                .filter(entry -> hasSuddenActivity(entry.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sortByTimestamp(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private WhaleTrade toWhaleTrade(Trade trade) {
        return new WhaleTrade(
                trade.getWallet(),
                tradeAmountUsd(trade),
                toDateTime(trade.getTimestamp()),
                trade.getMarketId(),
                trade.getMarketTitle(),
                displaySide(trade),
                trade.getPrice()
        );
    }

    private boolean hasSuddenActivity(List<WhaleTrade> trades) {
        List<WhaleTrade> sortedTrades = sortByTimestamp(trades).stream()
                .filter(trade -> trade.getTimestamp() != null)
                .toList();

        for (int start = 0; start < sortedTrades.size(); start++) {
            int end = start + SUDDEN_ACTIVITY_MIN_TRADES - 1;
            if (end >= sortedTrades.size()) {
                return false;
            }

            Duration window = Duration.between(
                    sortedTrades.get(start).getTimestamp(),
                    sortedTrades.get(end).getTimestamp()
            );

            if (window.compareTo(SUDDEN_ACTIVITY_WINDOW) <= 0) {
                return true;
            }
        }

        return false;
    }

    private List<WhaleTrade> sortByTimestamp(List<WhaleTrade> trades) {
        return trades.stream()
                .sorted(Comparator.comparing(
                        WhaleTrade::getTimestamp,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    private double tradeAmountUsd(Trade trade) {
        return Math.abs(trade.getUsdValue());
    }

    private LocalDateTime toDateTime(long epochSeconds) {
        if (epochSeconds <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
    }

    private String displaySide(Trade trade) {
        if (trade.getOutcome() != null && !trade.getOutcome().isBlank()) {
            return trade.getOutcome().trim().toUpperCase();
        }
        if (trade.getSide() != null && !trade.getSide().isBlank()) {
            return trade.getSide().trim().toUpperCase();
        }
        return null;
    }
}
