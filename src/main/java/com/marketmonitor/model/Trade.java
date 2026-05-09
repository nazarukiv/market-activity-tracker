package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    @JsonAlias({"id", "transactionHash", "transaction_hash"})
    private String id;

    @JsonAlias({"market", "marketId", "conditionId"})
    private String market;

    @JsonAlias({"marketTitle", "title", "question"})
    private String marketTitle;

    @JsonAlias({"proxyWallet", "wallet", "walletAddress", "user"})
    private String wallet;

    @JsonAlias({"side", "tradeSide"})
    private String side;

    @JsonAlias({"outcome", "outcomeTitle"})
    private String outcome;

    @JsonAlias({"timestamp", "matchTime", "match_time"})
    private long timestamp;

    private double price;

    @JsonAlias({"amount", "size"})
    private double amount;

    @JsonAlias({"usdcSize", "cashAmount"})
    private double usdcSize;

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

    public String getMarketTitle() {
        return marketTitle;
    }

    public String getWallet() {
        return wallet;
    }

    public String getSide() {
        return side;
    }

    public String getOutcome() {
        return outcome;
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

    public double getUsdValue() {
        if (usdcSize > 0) {
            return usdcSize;
        }
        if (amount <= 0) {
            return 0;
        }
        if (price > 0) {
            return amount * price;
        }
        return amount;
    }
}
