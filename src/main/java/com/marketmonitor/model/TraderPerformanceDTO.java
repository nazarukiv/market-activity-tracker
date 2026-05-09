package com.marketmonitor.model;

public class TraderPerformanceDTO {

    private String wallet;
    private double totalVolume;
    private Integer tradesCount;
    private String period;

    public TraderPerformanceDTO(String wallet, double totalVolume, Integer tradesCount, String period) {
        this.wallet = wallet;
        this.totalVolume = totalVolume;
        this.tradesCount = tradesCount;
        this.period = period;
    }

    public String getWallet() {
        return wallet;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public Integer getTradesCount() {
        return tradesCount;
    }

    public String getPeriod() {
        return period;
    }
}
