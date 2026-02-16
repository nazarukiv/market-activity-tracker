package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trader {

    @JsonProperty("proxy_wallet")
    private String walletAddress;

    private String username;
    private double volume;
    private double profit;
    private int trades;

    public Trader() {}


    //getters
    public String getWalletAddress() {
        return walletAddress;
    }

    public String getUsername() {
        return username;
    }

    public double getVolume() {
        return volume;
    }

    public double getProfit() {
        return profit;
    }

    public int getTrades() {
        return trades;
    }
}