package com.example.travel_panner_project;

public class TransportOption {
    private String type;
    private String provider;
    private String fare;
    private String duration;

    public TransportOption(String type, String provider, String fare, String duration) {
        this.type = type;
        this.provider = provider;
        this.fare = fare;
        this.duration = duration;
    }

    public String getType() { return type; }
    public String getProvider() { return provider; }
    public String getFare() { return fare; }
    public String getDuration() { return duration; }
}
