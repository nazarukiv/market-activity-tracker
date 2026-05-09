package com.marketmonitor.service;

import com.marketmonitor.exception.InvalidWalletAddressException;
import com.marketmonitor.model.WalletSearchResult;
import com.marketmonitor.util.WalletAddressUtils;
import org.springframework.stereotype.Service;

@Service
public class WalletSearchService {

    public WalletSearchResult resolve(String wallet) {
        try {
            return WalletSearchResult.valid(WalletAddressUtils.normalize(wallet));
        } catch (InvalidWalletAddressException e) {
            return WalletSearchResult.invalid(e.getMessage());
        }
    }
}
