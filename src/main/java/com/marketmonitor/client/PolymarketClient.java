package com.marketmonitor.client;

import com.marketmonitor.model.Trade;
import com.marketmonitor.model.Trader;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class PolymarketClient {

    private static final String DATA_API_URL = "https://data-api.polymarket.com";
    private static final int DEFAULT_TRADE_LIMIT = 500;
    private static final double WHALE_TRADE_MIN_AMOUNT_USD = 10000.0;

    private final RestTemplate restTemplate;

    public PolymarketClient() {
        this(new RestTemplate());
    }

    public PolymarketClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Trader> fetchTopTraders(String timePeriod, int limit) {
        String url = DATA_API_URL + "/v1/leaderboard?timePeriod=" + timePeriod
                + "&orderBy=VOL&limit=" + limit;

        try {
            Trader[] traders = restTemplate.getForObject(url, Trader[].class);
            if (traders == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(traders);
        } catch (RestClientException e) {
            System.err.println("Failed to fetch leaderboard: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Trade> fetchTradesForTrader(String walletAddress) {
        return fetchTradesForTrader(walletAddress, DEFAULT_TRADE_LIMIT);
    }

    public List<Trade> getRecentTrades() {
        String url = DATA_API_URL + "/trades?limit=" + DEFAULT_TRADE_LIMIT
                + "&filterType=CASH&filterAmount=" + WHALE_TRADE_MIN_AMOUNT_USD;

        try {
            Trade[] trades = restTemplate.getForObject(url, Trade[].class);
            if (trades == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(trades);
        } catch (RestClientException e) {
            System.err.println("Failed to fetch recent trades: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Trade> fetchTradesForTrader(String walletAddress, int limit) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Collections.emptyList();
        }

        String encodedWallet = URLEncoder.encode(walletAddress, StandardCharsets.UTF_8);
        String url = DATA_API_URL + "/trades?user=" + encodedWallet
                + "&sortBy=TIMESTAMP&sortDirection=DESC&limit=" + limit;

        try {
            Trade[] trades = restTemplate.getForObject(url, Trade[].class);
            if (trades == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(trades);
        } catch (RestClientException e) {
            System.err.println("Failed to fetch trades for " + walletAddress + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
