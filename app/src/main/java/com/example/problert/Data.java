package com.example.problert;

public class Data {
    private final String title;
    private final String description;
    private final String lat;
    private final String lng;

    public Data(String title, String description, String lat, String lng) {
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
