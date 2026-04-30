package com.marketmonitor.model;

import java.time.LocalDateTime;

public class WhaleTrade {

    private final String wallet;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String marketId;
    private final double price;

    public WhaleTrade(String wallet, double amount, LocalDateTime timestamp, String marketId, double price) {
        this.wallet = wallet;
        this.amount = amount;
        this.timestamp = timestamp;
        this.marketId = marketId;
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

    public double getPrice() {
        return price;
    }
}
