package com.marketmonitor.model;

public record WalletSearchResult(boolean valid, String normalizedWallet, String errorMessage) {

    public static WalletSearchResult valid(String normalizedWallet) {
        return new WalletSearchResult(true, normalizedWallet, null);
    }

    public static WalletSearchResult invalid(String errorMessage) {
        return new WalletSearchResult(false, null, errorMessage);
    }
}
