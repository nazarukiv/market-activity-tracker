package com.marketmonitor.controller;

import com.marketmonitor.model.WalletSearchResult;
import com.marketmonitor.service.WalletSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

@Controller
public class WalletSearchController {

    private final WalletSearchService walletSearchService;

    public WalletSearchController(WalletSearchService walletSearchService) {
        this.walletSearchService = walletSearchService;
    }

    @GetMapping("/wallet/search")
    public String searchWallet(
            @RequestParam(name = "wallet", required = false) String wallet,
            @RequestHeader(name = "Referer", required = false) String referer,
            RedirectAttributes redirectAttributes
    ) {
        WalletSearchResult result = walletSearchService.resolve(wallet);
        if (result.valid()) {
            return "redirect:/trader/" + result.normalizedWallet();
        }

        redirectAttributes.addFlashAttribute("walletSearchError", result.errorMessage());
        redirectAttributes.addFlashAttribute("walletSearchValue", wallet == null ? "" : wallet.trim());
        return redirectBack(referer);
    }

    private String redirectBack(String referer) {
        if (referer == null || referer.isBlank()) {
            return "redirect:/traders";
        }

        try {
            URI uri = URI.create(referer);
            String path = uri.getPath();
            if (path == null || path.isBlank() || "/wallet/search".equals(path)) {
                return "redirect:/traders";
            }

            String query = uri.getRawQuery();
            return "redirect:" + path + (query == null || query.isBlank() ? "" : "?" + query);
        } catch (IllegalArgumentException e) {
            return "redirect:/traders";
        }
    }
}
