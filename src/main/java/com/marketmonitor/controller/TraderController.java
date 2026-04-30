package com.marketmonitor.controller;

import com.marketmonitor.model.TraderPerformanceDTO;
import com.marketmonitor.service.TraderAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TraderController {

    private final TraderAnalysisService traderAnalysisService;

    public TraderController(TraderAnalysisService traderAnalysisService) {
        this.traderAnalysisService = traderAnalysisService;
    }

    @GetMapping("/traders/top/weekly")
    @ResponseBody
    public List<TraderPerformanceDTO> getTopTradersWeekly() {
        return traderAnalysisService.getTopTradersWeekly();
    }

    @GetMapping("/traders/top/monthly")
    @ResponseBody
    public List<TraderPerformanceDTO> getTopTradersMonthly() {
        return traderAnalysisService.getTopTradersMonthly();
    }

    @GetMapping("/traders/top/half-year")
    @ResponseBody
    public List<TraderPerformanceDTO> getTopTradersHalfYear() {
        return traderAnalysisService.getTopTradersHalfYear();
    }

    @GetMapping("/traders")
    public String showTraders(Model model) {
        model.addAttribute("weeklyTraders", traderAnalysisService.getTopTradersWeekly());
        model.addAttribute("monthlyTraders", traderAnalysisService.getTopTradersMonthly());
        model.addAttribute("halfYearTraders", traderAnalysisService.getTopTradersHalfYear());
        return "traders";
    }
}
