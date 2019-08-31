package com.example.problert;

public class ImageId {
    boolean success;
    String image;

    public ImageId(boolean success, String image) {
        this.success = success;
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
