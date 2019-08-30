package com.example.problert;

public class Coordinate {
    String type;
    double coordinates[];

    public Coordinate(String type, double coordinates[]) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }
}
