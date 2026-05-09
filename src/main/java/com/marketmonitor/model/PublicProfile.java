package com.marketmonitor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicProfile {

    @JsonAlias({"proxyWallet", "wallet", "walletAddress", "address"})
    private String wallet;

    private String name;
    private String pseudonym;
    private String bio;
    private String xUsername;
    private boolean verifiedBadge;

    public PublicProfile() {}

    public String getWallet() {
        return wallet;
    }

    public String getName() {
        return name;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public String getBio() {
        return bio;
    }

    public String getXUsername() {
        return xUsername;
    }

    public boolean isVerifiedBadge() {
        return verifiedBadge;
    }
}
