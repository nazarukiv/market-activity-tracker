package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradedMarketsResponse {

    private String user;
    private Integer traded;

    public TradedMarketsResponse() {}

    public String getUser() {
        return user;
    }

    public Integer getTraded() {
        return traded;
    }
}
