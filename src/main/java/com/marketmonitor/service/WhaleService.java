package com.marketmonitor.service;

import com.marketmonitor.analyzer.WhaleAnalyzer;
import com.marketmonitor.client.PolymarketClient;
import com.marketmonitor.model.Trade;
import com.marketmonitor.model.WhaleTrade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WhaleService {

    private final PolymarketClient polymarketClient;
    private final WhaleAnalyzer whaleAnalyzer;

    public WhaleService(PolymarketClient polymarketClient, WhaleAnalyzer whaleAnalyzer) {
        this.polymarketClient = polymarketClient;
        this.whaleAnalyzer = whaleAnalyzer;
    }

    public List<WhaleTrade> getRecentWhales() {
        List<Trade> trades = polymarketClient.getRecentTrades();
        return whaleAnalyzer.findWhaleTrades(trades);
    }

    public Map<String, List<WhaleTrade>> getActiveWhales() {
        return whaleAnalyzer.detectSuddenActivity(getRecentWhales());
    }
}
