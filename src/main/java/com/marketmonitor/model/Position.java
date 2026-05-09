package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    @JsonAlias({"proxyWallet", "wallet", "walletAddress", "user"})
    private String wallet;

    @JsonAlias({"conditionId", "market", "marketId"})
    private String market;

    @JsonAlias({"title", "marketTitle", "question"})
    private String title;

    @JsonAlias({"outcome", "outcomeTitle"})
    private String outcome;

    private long timestamp;
    private double size;
    private double avgPrice;
    private double initialValue;
    private double currentValue;
    private double cashPnl;
    private double totalBought;
    private double realizedPnl;
    private double curPrice;

    public Position() {}

    public String getWallet() {
        return wallet;
    }

    public String getMarket() {
        return market;
    }

    public String getTitle() {
        return title;
    }

    public String getOutcome() {
        return outcome;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getSize() {
        return size;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public double getCashPnl() {
        return cashPnl;
    }

    public double getTotalBought() {
        return totalBought;
    }

    public double getRealizedPnl() {
        return realizedPnl;
    }

    public double getCurPrice() {
        return curPrice;
    }

    public String getMarketLabel() {
        if (title != null && !title.isBlank()) {
            return title;
        }
        if (market != null && !market.isBlank()) {
            return market;
        }
        return "Unknown market";
    }

    public double getNotionalValue() {
        if (totalBought > 0) {
            return totalBought;
        }
        if (initialValue > 0) {
            return initialValue;
        }
        if (currentValue > 0) {
            return currentValue;
        }
        double positionPrice = avgPrice > 0 ? avgPrice : curPrice;
        if (size > 0 && positionPrice > 0) {
            return size * positionPrice;
        }
        return 0;
    }
}
