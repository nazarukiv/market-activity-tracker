import api.PolymarketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Market;
import model.Trader;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Market Activity Tracker ===\n");

        System.out.println("Fetching active markets...");
        String marketsJson = PolymarketClient.fetchMarkets();
        ObjectMapper mapper = new ObjectMapper();
        Market[] markets = mapper.readValue(marketsJson, Market[].class);

        System.out.println("Active markets found: " + markets.length);
        for (int i = 0; i < Math.min(3, markets.length); i++) {
            System.out.println("  - " + markets[i].getQuestion());
        }

        System.out.println("\n" + "=".repeat(50) + "\n");

        System.out.println("Fetching top traders (7 days)...");
        // change: timePeriod="WEEK", orderBy="VOL"
        String leaderboardJson = PolymarketClient.fetchLeaderboard("WEEK", "VOL", 10);

        System.out.println("RAW JSON RESPONSE:");
        System.out.println(leaderboardJson);
        System.out.println("\n" + "=".repeat(50) + "\n");

        // Trader[] traders = mapper.readValue(leaderboardJson, Trader[].class);
    }
}