package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    private String id;
    private String market;
    private long timestamp;
    private double price;
    private double size;

    public Trade() {}

    public String getId() {
        return id;
    }

    public String getMarket() {
        return market;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }
}
