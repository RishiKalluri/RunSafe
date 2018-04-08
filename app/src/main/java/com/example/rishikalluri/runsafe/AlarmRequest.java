package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/8/2018.
 */

public class AlarmRequest {

    @SerializedName("services")
    private Services service;

    @SerializedName("location.coordinates")
    private Locations locations;

    public AlarmRequest(Services service, Locations locations) {
        this.service = service;
        this.locations = locations;
    }
}
