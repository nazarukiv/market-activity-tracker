package com.marketmonitor.controller;

import com.marketmonitor.model.WhaleTrade;
import com.marketmonitor.service.WhaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class WhaleController {

    private final WhaleService whaleService;

    public WhaleController(WhaleService whaleService) {
        this.whaleService = whaleService;
    }

    @GetMapping("/whales")
    @ResponseBody
    public List<WhaleTrade> getRecentWhales() {
        return whaleService.getRecentWhales();
    }

    @GetMapping("/whales/active")
    @ResponseBody
    public Map<String, List<WhaleTrade>> getActiveWhales() {
        return whaleService.getActiveWhales();
    }

    @GetMapping("/whales/view")
    public String showWhales(Model model) {
        List<WhaleTrade> recentWhales = whaleService.getRecentWhales();
        model.addAttribute("recentWhales", recentWhales);
        model.addAttribute("activeWhales", whaleService.getActiveWhales(recentWhales));
        return "whales";
    }
}
