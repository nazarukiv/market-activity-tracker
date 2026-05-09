package com.marketmonitor.service;

import com.marketmonitor.client.PolymarketClient;
import com.marketmonitor.model.Trade;
import com.marketmonitor.model.Trader;
import com.marketmonitor.model.TraderPerformanceDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
public class TraderAnalysisService {

    private static final int LEADERBOARD_LIMIT = 25;
    private static final int TRADE_LIMIT = 500;
    private static final String HALF_YEAR_PERIOD = "HALF_YEAR";

    private final PolymarketClient polymarketClient;

    public TraderAnalysisService(PolymarketClient polymarketClient) {
        this.polymarketClient = polymarketClient;
    }

    public List<TraderPerformanceDTO> getTopTradersWeekly() {
        return getTopTraders("WEEK", 7, "WEEK");
    }

    public List<TraderPerformanceDTO> getTopTradersMonthly() {
        return getTopTraders("MONTH", 30, "MONTH");
    }

    public List<TraderPerformanceDTO> getTopTradersHalfYear() {
        return getTopTraders("ALL", 180, "HALF_YEAR");
    }

    private List<TraderPerformanceDTO> getTopTraders(String leaderboardPeriod, int days, String period) {
        LocalDateTime startTime = LocalDateTime.now(ZoneOffset.UTC).minusDays(days);

        return polymarketClient.fetchTopTraders(leaderboardPeriod, LEADERBOARD_LIMIT).stream()
                .filter(trader -> trader.getWallet() != null && !trader.getWallet().isBlank())
                .map(trader -> calculatePerformance(trader, startTime, period))
                .filter(result -> result.getTotalVolume() > 0
                        || (result.getTradesCount() != null && result.getTradesCount() > 0))
                .sorted(Comparator.comparingDouble(TraderPerformanceDTO::getTotalVolume).reversed())
                .toList();
    }

    private TraderPerformanceDTO calculatePerformance(Trader trader, LocalDateTime startTime, String period) {
        if (!HALF_YEAR_PERIOD.equals(period)) {
            return new TraderPerformanceDTO(
                    trader.getWallet(),
                    trader.getVolume(),
                    trader.getTradesCount(),
                    period
            );
        }

        List<Trade> trades = polymarketClient.fetchTradesForTrader(trader.getWallet(), TRADE_LIMIT);

        List<Trade> recentTrades = trades.stream()
                .filter(trade -> toDateTime(trade.getTimestamp()).isAfter(startTime))
                .toList();

        if (recentTrades.isEmpty()) {
            return new TraderPerformanceDTO(
                    trader.getWallet(),
                    trader.getVolume(),
                    trader.getTradesCount(),
                    period
            );
        }

        double totalVolume = recentTrades.stream()
                .mapToDouble(Trade::getUsdValue)
                .sum();

        return new TraderPerformanceDTO(
                trader.getWallet(),
                totalVolume,
                recentTrades.size(),
                period
        );
    }

    private LocalDateTime toDateTime(long epochSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
    }
}
