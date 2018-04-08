package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/6/2018.
 */

class Alarm {

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String creation;

    @SerializedName("services")
    private Services services;

    @SerializedName("locations")
    private LocationResponse locationResponse;

    public String getId() {
        return id;
    }
}
