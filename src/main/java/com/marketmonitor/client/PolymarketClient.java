package com.marketmonitor.client;

import com.marketmonitor.model.Position;
import com.marketmonitor.model.PublicProfile;
import com.marketmonitor.model.Trade;
import com.marketmonitor.model.Trader;
import com.marketmonitor.model.TradedMarketsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class PolymarketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolymarketClient.class);

    private static final String DATA_API_URL = "https://data-api.polymarket.com";
    private static final String GAMMA_API_URL = "https://gamma-api.polymarket.com";
    private static final int DEFAULT_TRADE_LIMIT = 500;
    private static final int TRADE_PAGE_SIZE = 500;
    private static final int TRADE_MAX_LIMIT = 10_000;
    private static final int LEADERBOARD_MAX_LIMIT = 50;
    private static final int CURRENT_POSITION_PAGE_SIZE = 500;
    private static final int CLOSED_POSITION_PAGE_SIZE = 50;
    private static final int MAX_POSITION_PAGES = 20;
    private static final double WHALE_TRADE_MIN_AMOUNT_USD = 10000.0;

    private final RestTemplate restTemplate;

    public PolymarketClient() {
        this(new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build());
    }

    PolymarketClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Trader> fetchTopTraders(String timePeriod, int limit) {
        String safePeriod = encode(timePeriod == null || timePeriod.isBlank() ? "WEEK" : timePeriod);
        int safeLimit = clamp(limit, 1, LEADERBOARD_MAX_LIMIT);
        String url = DATA_API_URL + "/v1/leaderboard?category=OVERALL"
                + "&timePeriod=" + safePeriod
                + "&orderBy=VOL"
                + "&limit=" + safeLimit
                + "&offset=0";

        return getList(url, Trader[].class, "leaderboard " + timePeriod);
    }

    public Optional<Trader> fetchTraderLeaderboardEntry(String walletAddress, String timePeriod) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Optional.empty();
        }

        String safePeriod = encode(timePeriod == null || timePeriod.isBlank() ? "ALL" : timePeriod);
        String url = DATA_API_URL + "/v1/leaderboard?category=OVERALL"
                + "&timePeriod=" + safePeriod
                + "&orderBy=VOL"
                + "&limit=1"
                + "&offset=0"
                + "&user=" + encode(walletAddress);

        return getList(url, Trader[].class, "leaderboard entry for " + walletAddress).stream()
                .findFirst();
    }

    public List<Trade> fetchTradesForTrader(String walletAddress) {
        return fetchTradesForTrader(walletAddress, DEFAULT_TRADE_LIMIT);
    }

    public List<Trade> getRecentTrades() {
        String url = DATA_API_URL + "/trades?limit=" + DEFAULT_TRADE_LIMIT
                + "&takerOnly=true"
                + "&filterType=CASH&filterAmount=" + WHALE_TRADE_MIN_AMOUNT_USD;

        return getList(url, Trade[].class, "recent whale-sized trades");
    }

    public List<Trade> fetchTradesForTrader(String walletAddress, int limit) {
        return fetchTradesForTrader(walletAddress, limit, 0);
    }

    public List<Trade> fetchAllTradesForTrader(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Collections.emptyList();
        }

        List<Trade> allTrades = new ArrayList<>();
        Set<String> seenTradeKeys = new HashSet<>();

        for (int offset = 0; offset < TRADE_MAX_LIMIT; offset += TRADE_PAGE_SIZE) {
            List<Trade> trades = fetchTradesForTrader(walletAddress, TRADE_PAGE_SIZE, offset);
            if (trades.isEmpty()) {
                break;
            }

            int previousSize = allTrades.size();
            for (Trade trade : trades) {
                if (!belongsToWallet(trade, walletAddress)) {
                    continue;
                }

                if (seenTradeKeys.add(tradeKey(trade))) {
                    allTrades.add(trade);
                }
            }

            if (trades.size() < TRADE_PAGE_SIZE || allTrades.size() == previousSize) {
                break;
            }
        }

        return allTrades;
    }

    public List<Trade> fetchTradesForTrader(String walletAddress, int limit, int offset) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Collections.emptyList();
        }

        int safeLimit = clamp(limit, 1, TRADE_MAX_LIMIT);
        int safeOffset = clamp(offset, 0, TRADE_MAX_LIMIT);
        String encodedWallet = encode(walletAddress);
        String url = DATA_API_URL + "/trades?user=" + encodedWallet
                + "&takerOnly=true"
                + "&limit=" + safeLimit
                + "&offset=" + safeOffset;

        return getList(url, Trade[].class, "trades for " + walletAddress);
    }

    public List<Position> fetchCurrentPositionsForTrader(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Collections.emptyList();
        }

        return fetchPositionPages(
                DATA_API_URL + "/positions?user=" + encode(walletAddress)
                        + "&sortBy=CURRENT&sortDirection=DESC",
                CURRENT_POSITION_PAGE_SIZE,
                "current positions for " + walletAddress
        );
    }

    public List<Position> fetchClosedPositionsForTrader(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Collections.emptyList();
        }

        return fetchPositionPages(
                DATA_API_URL + "/closed-positions?user=" + encode(walletAddress)
                        + "&sortBy=TIMESTAMP&sortDirection=DESC",
                CLOSED_POSITION_PAGE_SIZE,
                "closed positions for " + walletAddress
        );
    }

    public Optional<Integer> fetchTradedMarketsCount(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Optional.empty();
        }

        String url = DATA_API_URL + "/traded?user=" + encode(walletAddress);
        return getObject(url, TradedMarketsResponse.class, "traded market count for " + walletAddress)
                .map(TradedMarketsResponse::getTraded);
    }

    public Optional<PublicProfile> fetchPublicProfile(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            return Optional.empty();
        }

        String url = GAMMA_API_URL + "/public-profile?address=" + encode(walletAddress);
        return getObject(url, PublicProfile.class, "public profile for " + walletAddress);
    }

    private boolean belongsToWallet(Trade trade, String walletAddress) {
        if (trade.getWallet() == null || trade.getWallet().isBlank()) {
            return true;
        }
        return walletAddress.equalsIgnoreCase(trade.getWallet());
    }

    private String tradeKey(Trade trade) {
        if (trade.getId() != null && !trade.getId().isBlank()) {
            return trade.getId();
        }
        return trade.getWallet() + "|"
                + trade.getMarket() + "|"
                + trade.getMarketTitle() + "|"
                + trade.getSide() + "|"
                + trade.getTimestamp() + "|"
                + trade.getPrice() + "|"
                + trade.getAmount();
    }

    private List<Position> fetchPositionPages(String baseUrl, int pageSize, String context) {
        List<Position> positions = new ArrayList<>();
        for (int page = 0; page < MAX_POSITION_PAGES; page++) {
            int offset = page * pageSize;
            List<Position> pagePositions = getList(
                    baseUrl + "&limit=" + pageSize + "&offset=" + offset,
                    Position[].class,
                    context + " page " + (page + 1)
            );
            positions.addAll(pagePositions);

            if (pagePositions.size() < pageSize) {
                break;
            }
        }
        return positions;
    }

    private <T> List<T> getList(String url, Class<T[]> responseType, String context) {
        try {
            T[] response = restTemplate.getForObject(url, responseType);
            if (response == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(response);
        } catch (RestClientException e) {
            LOGGER.warn("Failed to fetch {}: {}", context, e.getMessage());
            return Collections.emptyList();
        }
    }

    private <T> Optional<T> getObject(String url, Class<T> responseType, String context) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, responseType));
        } catch (RestClientException e) {
            LOGGER.warn("Failed to fetch {}: {}", context, e.getMessage());
            return Optional.empty();
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
