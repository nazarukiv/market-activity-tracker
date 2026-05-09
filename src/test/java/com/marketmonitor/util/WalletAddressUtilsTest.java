package com.marketmonitor.util;

import com.marketmonitor.exception.InvalidWalletAddressException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletAddressUtilsTest {

    @Test
    void normalizesTrimmedMixedCaseWallet() {
        String wallet = "  0X1234567890ABCDEF1234567890ABCDEF12345678  ";

        String normalizedWallet = WalletAddressUtils.normalize(wallet);

        assertEquals("0x1234567890abcdef1234567890abcdef12345678", normalizedWallet);
    }

    @Test
    void rejectsBlankWallet() {
        assertThrows(InvalidWalletAddressException.class, () -> WalletAddressUtils.normalize(" "));
    }

    @Test
    void rejectsWalletWithoutPrefix() {
        assertThrows(InvalidWalletAddressException.class, () -> WalletAddressUtils.normalize("1234567890abcdef1234567890abcdef12345678"));
    }

    @Test
    void rejectsWrongLengthWallet() {
        assertThrows(InvalidWalletAddressException.class, () -> WalletAddressUtils.normalize("0x1234"));
    }

    @Test
    void rejectsNonHexWallet() {
        assertThrows(InvalidWalletAddressException.class, () -> WalletAddressUtils.normalize("0x1234567890abcdef1234567890abcdef1234567g"));
    }
}
