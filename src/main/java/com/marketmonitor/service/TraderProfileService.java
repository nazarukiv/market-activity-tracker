package com.marketmonitor.service;

import com.marketmonitor.client.PolymarketClient;
import com.marketmonitor.model.Position;
import com.marketmonitor.model.PublicProfile;
import com.marketmonitor.model.Trade;
import com.marketmonitor.model.Trader;
import com.marketmonitor.model.TraderProfile;
import com.marketmonitor.util.WalletAddressUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TraderProfileService {

    private static final int RELATED_TRADER_LIMIT = 6;
    private static final int RECENT_ACTIVITY_LIMIT = 8;
    private static final int TOP_MARKET_LIMIT = 6;
    private static final double WHALE_TRADE_THRESHOLD_USD = 10_000.0;

    private final PolymarketClient polymarketClient;

    public TraderProfileService(PolymarketClient polymarketClient) {
        this.polymarketClient = polymarketClient;
    }

    public TraderProfile getTraderProfile(String wallet) {
        String normalizedWallet = normalizeWallet(wallet);

        List<Trade> trades = polymarketClient.fetchAllTradesForTrader(normalizedWallet);
        List<Position> currentPositions = polymarketClient.fetchCurrentPositionsForTrader(normalizedWallet);
        List<Position> closedPositions = polymarketClient.fetchClosedPositionsForTrader(normalizedWallet);
        Optional<Trader> leaderboardTrader = polymarketClient.fetchTraderLeaderboardEntry(normalizedWallet, "ALL");
        Optional<PublicProfile> publicProfile = polymarketClient.fetchPublicProfile(normalizedWallet);

        double tradingVolume = tradingVolume(trades, leaderboardTrader);
        Integer totalPositions = totalPositions(trades, currentPositions, closedPositions);
        Double averagePositionSize = totalPositions != null && totalPositions > 0
                ? money(tradingVolume / totalPositions)
                : null;
        Integer marketsTraded = polymarketClient.fetchTradedMarketsCount(normalizedWallet)
                .orElseGet(() -> derivedMarketsTraded(trades, currentPositions, closedPositions));
        Double pnl = pnl(currentPositions, closedPositions, leaderboardTrader);
        Double winRate = winRate(closedPositions);
        String nickname = nicknameFor(normalizedWallet, publicProfile, leaderboardTrader);
        boolean limitedData = trades.isEmpty() && currentPositions.isEmpty() && closedPositions.isEmpty();

        return new TraderProfile(
                normalizedWallet,
                shortWallet(normalizedWallet),
                nickname,
                badges(publicProfile, leaderboardTrader, trades, currentPositions, closedPositions, limitedData),
                summaryFor(nickname, publicProfile, tradingVolume, marketsTraded, limitedData),
                pnl,
                tradingVolume,
                winRate,
                totalPositions,
                averagePositionSize,
                marketsTraded,
                unavailableRiskMetrics(),
                activityFromTrades(trades),
                topMarkets(trades, currentPositions, closedPositions),
                relatedTraders(normalizedWallet),
                limitedData
        );
    }

    private double tradingVolume(List<Trade> trades, Optional<Trader> leaderboardTrader) {
        double tradeVolume = trades.stream()
                .mapToDouble(Trade::getUsdValue)
                .sum();
        if (tradeVolume > 0) {
            return money(tradeVolume);
        }
        return leaderboardTrader
                .map(Trader::getVolume)
                .filter(volume -> volume > 0)
                .map(this::money)
                .orElse(0.0);
    }

    private Integer totalPositions(List<Trade> trades, List<Position> currentPositions, List<Position> closedPositions) {
        int positionCount = currentPositions.size() + closedPositions.size();
        if (positionCount > 0) {
            return positionCount;
        }
        if (!trades.isEmpty()) {
            return trades.size();
        }
        return null;
    }

    private Integer derivedMarketsTraded(List<Trade> trades, List<Position> currentPositions, List<Position> closedPositions) {
        long markets = distinctMarketLabels(trades, currentPositions, closedPositions);
        return markets > 0 ? Math.toIntExact(markets) : null;
    }

    private long distinctMarketLabels(List<Trade> trades, List<Position> currentPositions, List<Position> closedPositions) {
        return Stream.concat(
                        trades.stream().map(this::marketName),
                        Stream.concat(
                                currentPositions.stream().map(Position::getMarketLabel),
                                closedPositions.stream().map(Position::getMarketLabel)
                        )
                )
                .filter(label -> label != null && !label.isBlank())
                .distinct()
                .count();
    }

    private Double pnl(List<Position> currentPositions, List<Position> closedPositions, Optional<Trader> leaderboardTrader) {
        if (!currentPositions.isEmpty() || !closedPositions.isEmpty()) {
            double currentPnl = currentPositions.stream()
                    .mapToDouble(Position::getCashPnl)
                    .sum();
            double realizedPnl = closedPositions.stream()
                    .mapToDouble(Position::getRealizedPnl)
                    .sum();
            return money(currentPnl + realizedPnl);
        }

        return leaderboardTrader
                .map(Trader::getProfit)
                .map(this::money)
                .orElse(null);
    }

    private Double winRate(List<Position> closedPositions) {
        List<Position> resolvedPositions = closedPositions.stream()
                .filter(position -> position.getTotalBought() > 0 || position.getRealizedPnl() != 0)
                .toList();
        if (resolvedPositions.isEmpty()) {
            return null;
        }

        long wins = resolvedPositions.stream()
                .filter(position -> position.getRealizedPnl() > 0)
                .count();
        return money((wins * 100.0) / resolvedPositions.size());
    }

    private List<String> badges(
            Optional<PublicProfile> publicProfile,
            Optional<Trader> leaderboardTrader,
            List<Trade> trades,
            List<Position> currentPositions,
            List<Position> closedPositions,
            boolean limitedData
    ) {
        List<String> badges = new ArrayList<>();
        if (publicProfile.map(PublicProfile::isVerifiedBadge).orElse(false)
                || leaderboardTrader.map(Trader::isVerifiedBadge).orElse(false)) {
            badges.add("Verified");
        }
        if (leaderboardTrader.isPresent()) {
            badges.add("Leaderboard");
        }
        if (trades.stream().anyMatch(trade -> trade.getUsdValue() >= WHALE_TRADE_THRESHOLD_USD)) {
            badges.add("Whale activity");
        }
        if (!currentPositions.isEmpty()) {
            badges.add("Open positions");
        }
        if (!closedPositions.isEmpty()) {
            badges.add("Closed positions");
        }
        if (limitedData) {
            badges.add("Limited data");
        }
        if (badges.isEmpty()) {
            badges.add("Public wallet");
        }
        return badges;
    }

    private TraderProfile.RiskMetrics unavailableRiskMetrics() {
        return new TraderProfile.RiskMetrics(null, null, null, null);
    }

    private List<TraderProfile.Activity> activityFromTrades(List<Trade> trades) {
        return trades.stream()
                .limit(RECENT_ACTIVITY_LIMIT)
                .map(trade -> new TraderProfile.Activity(
                        marketName(trade),
                        sideFor(trade),
                        money(trade.getUsdValue()),
                        toDateTime(trade.getTimestamp()),
                        null
                ))
                .toList();
    }

    private List<TraderProfile.MarketExposure> topMarkets(
            List<Trade> trades,
            List<Position> currentPositions,
            List<Position> closedPositions
    ) {
        Map<String, MarketAccumulator> markets = new LinkedHashMap<>();
        Map<String, ClosedMarketStats> closedStats = closedMarketStats(closedPositions);

        for (Trade trade : trades) {
            markets.computeIfAbsent(marketName(trade), ignored -> new MarketAccumulator())
                    .add(trade.getUsdValue());
        }

        if (markets.isEmpty()) {
            for (Position position : currentPositions) {
                markets.computeIfAbsent(position.getMarketLabel(), ignored -> new MarketAccumulator())
                        .add(position.getNotionalValue());
            }
            for (Position position : closedPositions) {
                markets.computeIfAbsent(position.getMarketLabel(), ignored -> new MarketAccumulator())
                        .add(position.getNotionalValue());
            }
        }

        return markets.entrySet().stream()
                .filter(entry -> entry.getValue().volume > 0)
                .sorted((first, second) -> Double.compare(second.getValue().volume, first.getValue().volume))
                .limit(TOP_MARKET_LIMIT)
                .map(entry -> new TraderProfile.MarketExposure(
                        entry.getKey(),
                        money(entry.getValue().volume),
                        Optional.ofNullable(closedStats.get(entry.getKey()))
                                .map(ClosedMarketStats::accuracy)
                                .orElse(null)
                ))
                .toList();
    }

    private Map<String, ClosedMarketStats> closedMarketStats(List<Position> closedPositions) {
        Map<String, ClosedMarketStats> stats = new LinkedHashMap<>();
        for (Position position : closedPositions) {
            stats.computeIfAbsent(position.getMarketLabel(), ignored -> new ClosedMarketStats())
                    .add(position.getRealizedPnl());
        }
        return stats;
    }

    private List<TraderProfile.RelatedTrader> relatedTraders(String wallet) {
        return polymarketClient.fetchTopTraders("WEEK", 25).stream()
                .filter(trader -> trader.getWallet() != null && !trader.getWallet().isBlank())
                .filter(trader -> !wallet.equalsIgnoreCase(trader.getWallet()))
                .limit(RELATED_TRADER_LIMIT)
                .map(trader -> new TraderProfile.RelatedTrader(
                        trader.getWallet(),
                        shortWallet(trader.getWallet()),
                        nicknameFor(trader),
                        money(trader.getVolume()),
                        null
                ))
                .toList();
    }

    private String summaryFor(
            String nickname,
            Optional<PublicProfile> publicProfile,
            double tradingVolume,
            Integer marketsTraded,
            boolean limitedData
    ) {
        Optional<String> bio = publicProfile
                .map(PublicProfile::getBio)
                .filter(value -> !value.isBlank());
        if (bio.isPresent()) {
            return bio.get();
        }

        String marketCopy = marketsTraded == null
                ? "an unavailable market count"
                : marketsTraded + " tracked markets";
        String volumeCopy = tradingVolume > 0
                ? " $" + Math.round(tradingVolume) + " in public tracked volume"
                : " unavailable tracked volume";
        String dataCopy = limitedData
                ? "Limited recent activity available for this wallet. Unavailable metrics are marked N/A."
                : "Unavailable metrics are marked N/A instead of estimated.";
        return nickname + " is shown from public Polymarket data across " + marketCopy
                + " with" + volumeCopy + ". " + dataCopy;
    }

    private String nicknameFor(String wallet, Optional<PublicProfile> publicProfile, Optional<Trader> leaderboardTrader) {
        return publicProfile
                .map(PublicProfile::getName)
                .filter(name -> !name.isBlank())
                .or(() -> publicProfile
                        .map(PublicProfile::getPseudonym)
                        .filter(name -> !name.isBlank()))
                .or(() -> leaderboardTrader
                        .map(this::nicknameFor)
                        .filter(name -> !name.isBlank()))
                .orElse("Trader " + suffix(wallet));
    }

    private String nicknameFor(Trader trader) {
        if (trader.getUserName() != null && !trader.getUserName().isBlank()) {
            return trader.getUserName();
        }
        if (trader.getXUsername() != null && !trader.getXUsername().isBlank()) {
            return "@" + trader.getXUsername();
        }
        return "Trader " + suffix(trader.getWallet());
    }

    private String sideFor(Trade trade) {
        if (trade.getOutcome() != null && !trade.getOutcome().isBlank()) {
            return trade.getOutcome().trim().toUpperCase();
        }
        if (trade.getSide() != null && !trade.getSide().isBlank()) {
            return trade.getSide().trim().toUpperCase();
        }
        return "N/A";
    }

    private String marketName(Trade trade) {
        if (trade.getMarketTitle() != null && !trade.getMarketTitle().isBlank()) {
            return trade.getMarketTitle();
        }
        if (trade.getMarket() != null && !trade.getMarket().isBlank()) {
            return trade.getMarket();
        }
        return "Unknown market";
    }

    private LocalDateTime toDateTime(long epochSeconds) {
        if (epochSeconds <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC).withNano(0);
    }

    private String normalizeWallet(String wallet) {
        return WalletAddressUtils.normalize(wallet);
    }

    private String shortWallet(String wallet) {
        if (wallet == null || wallet.length() <= 14) {
            return wallet;
        }
        return wallet.substring(0, 6) + "..." + wallet.substring(wallet.length() - 4);
    }

    private String suffix(String wallet) {
        if (wallet == null || wallet.length() < 4) {
            return "0000";
        }
        return wallet.substring(Math.max(0, wallet.length() - 4)).toUpperCase();
    }

    private double money(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class MarketAccumulator {
        private double volume;

        private void add(double amount) {
            volume += Math.max(0, amount);
        }
    }

    private static class ClosedMarketStats {
        private int total;
        private int wins;

        private void add(double realizedPnl) {
            total++;
            if (realizedPnl > 0) {
                wins++;
            }
        }

        private Double accuracy() {
            if (total == 0) {
                return null;
            }
            return Math.round((wins * 10000.0) / total) / 100.0;
        }
    }
}
