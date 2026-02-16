package service;

import model.Trade;
import model.Trader;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UserAnalyzer {

    private static final long NEW_ACCOUNT_DAYS = 30;
    private static final double HIGH_VOLUME_THRESHOLD = 10000.0;

    public boolean isNewAccount(Trade firstTrade) {
        long accountAgeInDays = ChronoUnit.DAYS.between(
                Instant.ofEpochSecond(firstTrade.getTimestamp()),
                Instant.now()
        );

        return accountAgeInDays < NEW_ACCOUNT_DAYS;
    }

    public boolean isHighVolumeTrader(Trader trader) {
        return trader.getVolume() > HIGH_VOLUME_THRESHOLD;
    }

    public boolean isSuspicious(Trader trader, Trade firstTrade) {
        return isNewAccount(firstTrade) && isHighVolumeTrader(trader);
    }
}
