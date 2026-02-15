package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Market {

    private int id;
    private String question;
    private double volume;

    //empty constructor for jackson(required)
    public Market() {}

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public double getVolume() {
        return volume;
    }
}