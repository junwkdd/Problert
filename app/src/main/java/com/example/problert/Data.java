package com.example.problert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.BitmapDescriptor;

public class Data {
//    private final String title;
//    private final String description;
//    private final String lat;
//    private final String lng;
//
//    public Data(String title, String description, String lat, String lng) {
//        this.title = title;
//        this.description = description;
//        this.lat = lat;
//        this.lng = lng;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public String getLat() {
//        return lat;
//    }
//
//    public String getLng() {
//        return lng;
//    }
    int liked;
    String issueid;
    String title;
    String description;
    Coordinate coordinate;
    String createdAt;
    @Nullable String imageid;

    public Data(int liked, String issueid, String title, String description, Coordinate coordinate, String createdAt, @Nullable String imageid) {
        this.liked = liked;
        this.issueid = issueid;
        this.title = title;
        this.description = description;
        this.coordinate = coordinate;
        this.createdAt = createdAt;
        this.imageid = imageid;
    }

    public int getLiked() {
        return liked;
    }

    public String getIssueid() {
        return issueid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public String getImageid() {
        return imageid;
    }
}

