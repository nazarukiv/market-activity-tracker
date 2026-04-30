package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trader {

    private String id;
    private String rank;

    @JsonAlias({"proxyWallet", "walletAddress"})
    private String wallet;

    private String userName;

    @JsonProperty("xUsername")
    private String xUsername;

    @JsonProperty("verifiedBadge")
    private boolean verifiedBadge;

    @JsonAlias({"vol", "volume"})
    private double volume;

    @JsonAlias({"trades", "tradesCount"})
    private int tradesCount;

    @JsonAlias({"pnl", "profit"})
    private double profit;

    @JsonIgnore
    private LocalDateTime firstTradeTime;

    @JsonProperty("profileImage")
    private String profileImage;

    public Trader() {}

    public String getId() {
        return id;
    }

    public String getRank() {
        return rank;
    }

    public String getWallet() {
        return wallet;
    }

    public String getWalletAddress() {
        return wallet;
    }

    public String getUserName() {
        return userName;
    }

    public String getXUsername() {
        return xUsername;
    }

    public boolean isVerifiedBadge() {
        return verifiedBadge;
    }

    public double getVolume() {
        return volume;
    }

    public int getTradesCount() {
        return tradesCount;
    }

    public double getProfit() {
        return profit;
    }

    public LocalDateTime getFirstTradeTime() {
        return firstTradeTime;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setFirstTradeTime(LocalDateTime firstTradeTime) {
        this.firstTradeTime = firstTradeTime;
    }
}
