package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    private String id;

    @JsonAlias({"market", "marketId", "conditionId"})
    private String market;

    @JsonAlias({"proxyWallet", "wallet", "walletAddress", "user"})
    private String wallet;

    private long timestamp;
    private double price;

    @JsonAlias({"amount", "size"})
    private double amount;

    public Trade() {}

    public String getId() {
        return id;
    }

    public String getMarket() {
        return market;
    }

    public String getMarketId() {
        return market;
    }

    public String getWallet() {
        return wallet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public double getSize() {
        return amount;
    }
}
