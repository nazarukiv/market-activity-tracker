package com.marketmonitor.model;

import java.time.LocalDateTime;
import java.util.List;

public class TraderProfile {

    private final String wallet;
    private final String shortWallet;
    private final String nickname;
    private final List<String> badges;
    private final String summary;
    private final Double estimatedPnl;
    private final double tradingVolume;
    private final Double winRate;
    private final Integer totalPositions;
    private final Double averagePositionSize;
    private final Integer marketsTraded;
    private final RiskMetrics riskMetrics;
    private final List<Activity> recentActivity;
    private final List<MarketExposure> topMarkets;
    private final List<RelatedTrader> relatedTraders;
    private final boolean limitedData;

    public TraderProfile(
            String wallet,
            String shortWallet,
            String nickname,
            List<String> badges,
            String summary,
            Double estimatedPnl,
            double tradingVolume,
            Double winRate,
            Integer totalPositions,
            Double averagePositionSize,
            Integer marketsTraded,
            RiskMetrics riskMetrics,
            List<Activity> recentActivity,
            List<MarketExposure> topMarkets,
            List<RelatedTrader> relatedTraders,
            boolean limitedData
    ) {
        this.wallet = wallet;
        this.shortWallet = shortWallet;
        this.nickname = nickname;
        this.badges = badges;
        this.summary = summary;
        this.estimatedPnl = estimatedPnl;
        this.tradingVolume = tradingVolume;
        this.winRate = winRate;
        this.totalPositions = totalPositions;
        this.averagePositionSize = averagePositionSize;
        this.marketsTraded = marketsTraded;
        this.riskMetrics = riskMetrics;
        this.recentActivity = recentActivity;
        this.topMarkets = topMarkets;
        this.relatedTraders = relatedTraders;
        this.limitedData = limitedData;
    }

    public String getWallet() {
        return wallet;
    }

    public String getShortWallet() {
        return shortWallet;
    }

    public String getNickname() {
        return nickname;
    }

    public List<String> getBadges() {
        return badges;
    }

    public String getSummary() {
        return summary;
    }

    public Double getEstimatedPnl() {
        return estimatedPnl;
    }

    public Double getAbsoluteEstimatedPnl() {
        return estimatedPnl == null ? null : Math.abs(estimatedPnl);
    }

    public double getTradingVolume() {
        return tradingVolume;
    }

    public Double getWinRate() {
        return winRate;
    }

    public Integer getTotalPositions() {
        return totalPositions;
    }

    public Double getAveragePositionSize() {
        return averagePositionSize;
    }

    public Integer getMarketsTraded() {
        return marketsTraded;
    }

    public RiskMetrics getRiskMetrics() {
        return riskMetrics;
    }

    public List<Activity> getRecentActivity() {
        return recentActivity;
    }

    public List<MarketExposure> getTopMarkets() {
        return topMarkets;
    }

    public List<RelatedTrader> getRelatedTraders() {
        return relatedTraders;
    }

    public boolean isLimitedData() {
        return limitedData;
    }

    public boolean isDemoData() {
        return false;
    }

    public static class Activity {
        private final String marketName;
        private final String side;
        private final double amount;
        private final LocalDateTime timestamp;
        private final Double pnl;

        public Activity(String marketName, String side, double amount, LocalDateTime timestamp, Double pnl) {
            this.marketName = marketName;
            this.side = side;
            this.amount = amount;
            this.timestamp = timestamp;
            this.pnl = pnl;
        }

        public String getMarketName() {
            return marketName;
        }

        public String getSide() {
            return side;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Double getPnl() {
            return pnl;
        }

        public Double getAbsolutePnl() {
            return pnl == null ? null : Math.abs(pnl);
        }

        public boolean isPositivePnl() {
            return pnl != null && pnl >= 0;
        }
    }

    public static class MarketExposure {
        private final String title;
        private final double totalVolume;
        private final Double accuracy;

        public MarketExposure(String title, double totalVolume, Double accuracy) {
            this.title = title;
            this.totalVolume = totalVolume;
            this.accuracy = accuracy;
        }

        public String getTitle() {
            return title;
        }

        public double getTotalVolume() {
            return totalVolume;
        }

        public Double getAccuracy() {
            return accuracy;
        }
    }

    public static class RiskMetrics {
        private final Integer riskScore;
        private final String averageHoldTime;
        private final Integer volatilityScore;
        private final Integer consistencyScore;

        public RiskMetrics(Integer riskScore, String averageHoldTime, Integer volatilityScore, Integer consistencyScore) {
            this.riskScore = riskScore;
            this.averageHoldTime = averageHoldTime;
            this.volatilityScore = volatilityScore;
            this.consistencyScore = consistencyScore;
        }

        public Integer getRiskScore() {
            return riskScore;
        }

        public String getAverageHoldTime() {
            return averageHoldTime;
        }

        public Integer getVolatilityScore() {
            return volatilityScore;
        }

        public Integer getConsistencyScore() {
            return consistencyScore;
        }
    }

    public static class RelatedTrader {
        private final String wallet;
        private final String shortWallet;
        private final String nickname;
        private final double volume;
        private final Double similarity;

        public RelatedTrader(String wallet, String shortWallet, String nickname, double volume, Double similarity) {
            this.wallet = wallet;
            this.shortWallet = shortWallet;
            this.nickname = nickname;
            this.volume = volume;
            this.similarity = similarity;
        }

        public String getWallet() {
            return wallet;
        }

        public String getShortWallet() {
            return shortWallet;
        }

        public String getNickname() {
            return nickname;
        }

        public double getVolume() {
            return volume;
        }

        public Double getSimilarity() {
            return similarity;
        }
    }
}
