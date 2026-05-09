package com.marketmonitor.util;

import com.marketmonitor.exception.InvalidWalletAddressException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class WalletAddressUtils {

    private static final int WALLET_LENGTH = 42;
    private static final Pattern WALLET_PATTERN = Pattern.compile("^0x[a-fA-F0-9]{40}$");

    private WalletAddressUtils() {}

    public static String normalize(String wallet) {
        if (wallet == null || wallet.trim().isEmpty()) {
            throw new InvalidWalletAddressException("Enter a wallet address to open a trader profile.");
        }

        String trimmedWallet = wallet.trim();
        if (!trimmedWallet.regionMatches(true, 0, "0x", 0, 2)) {
            throw new InvalidWalletAddressException("Wallet address must start with 0x.");
        }

        if (trimmedWallet.length() != WALLET_LENGTH) {
            throw new InvalidWalletAddressException("Wallet address must be 42 characters long, including 0x.");
        }

        String normalizedPrefixWallet = "0x" + trimmedWallet.substring(2);
        if (!WALLET_PATTERN.matcher(normalizedPrefixWallet).matches()) {
            throw new InvalidWalletAddressException("Wallet address can only contain hexadecimal characters after 0x.");
        }

        return normalizedPrefixWallet.toLowerCase(Locale.ROOT);
    }
}
