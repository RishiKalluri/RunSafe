package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/8/2018.
 */

public class CoordinateResponse {

    private double lat;
    private double lng;
    private int accuracy;

    @SerializedName("created_at")
    private String creation;

}
