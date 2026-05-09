package com.marketmonitor.model;

import java.time.LocalDateTime;

public class WhaleTrade {

    private final String wallet;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String marketId;
    private final String marketTitle;
    private final String side;
    private final double price;

    public WhaleTrade(String wallet, double amount, LocalDateTime timestamp, String marketId, String marketTitle, String side, double price) {
        this.wallet = wallet;
        this.amount = amount;
        this.timestamp = timestamp;
        this.marketId = marketId;
        this.marketTitle = marketTitle;
        this.side = side;
        this.price = price;
    }

    public String getWallet() {
        return wallet;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMarketId() {
        return marketId;
    }

    public String getMarketTitle() {
        return marketTitle;
    }

    public String getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public String getMarketLabel() {
        if (marketTitle != null && !marketTitle.isBlank()) {
            return marketTitle;
        }
        if (marketId != null && !marketId.isBlank()) {
            return marketId;
        }
        return "Unknown market";
    }
}
