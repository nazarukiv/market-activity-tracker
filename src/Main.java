import java.nio.file.Files;
import java.nio.file.Path;

import api.PolymarketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import model.Market;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Market Activity Tracker started.");
        String json = PolymarketClient.fetchMarkets();

        ObjectMapper mapper = new ObjectMapper();

        Market[] markets = mapper.readValue(json, Market[].class);

        System.out.println("Loaded markets: " + markets.length);
        System.out.println();

        for (int i = 0; i < Math.min(5, markets.length); i++) {
            Market m = markets[i];
            System.out.println(
                    "[" + m.getId() + "] " +
                            m.getQuestion() +
                            " | volume=" + m.getVolume()
            );
        }
    }
}
