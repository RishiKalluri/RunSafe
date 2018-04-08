package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/7/2018.
 */

public class Locations {

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("accuracy")
    private int accuracy;

    public Locations(double lat, double lng, int accuracy) {
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
    }
}
