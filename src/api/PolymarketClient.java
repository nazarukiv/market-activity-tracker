package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PolymarketClient {

    private static final String BASE_URL =
            "https://gamma-api.polymarket.com/markets?closed=false&active=true&limit=100";

    private static final String DATA_API_URL = "https://data-api.polymarket.com";

    public static String fetchMarkets() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static String fetchLeaderboard(String timePeriod, String orderBy, int limit) throws Exception {
        String url = DATA_API_URL + "/v1/leaderboard?timePeriod=" + timePeriod +
                "&orderBy=" + orderBy + "&limit=" + limit;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}