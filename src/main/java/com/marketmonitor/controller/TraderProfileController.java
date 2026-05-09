package com.marketmonitor.controller;

import com.marketmonitor.exception.InvalidWalletAddressException;
import com.marketmonitor.model.TraderProfile;
import com.marketmonitor.service.TraderProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class TraderProfileController {

    private final TraderProfileService traderProfileService;

    public TraderProfileController(TraderProfileService traderProfileService) {
        this.traderProfileService = traderProfileService;
    }

    @GetMapping("/trader/{wallet}")
    public String showTraderProfile(@PathVariable String wallet, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("profile", traderProfileService.getTraderProfile(wallet));
            return "trader-profile";
        } catch (InvalidWalletAddressException e) {
            redirectAttributes.addFlashAttribute("walletSearchError", e.getMessage());
            redirectAttributes.addFlashAttribute("walletSearchValue", wallet);
            return "redirect:/traders";
        }
    }

    @GetMapping("/trader/{wallet}/data")
    @ResponseBody
    public ResponseEntity<?> getTraderProfileData(@PathVariable String wallet) {
        try {
            TraderProfile profile = traderProfileService.getTraderProfile(wallet);
            return ResponseEntity.ok(profile);
        } catch (InvalidWalletAddressException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
