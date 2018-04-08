package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rishi Kalluri on 3/8/2018.
 */

public class LocationResponse {

    @SerializedName("coordinates")
    private ArrayList<CoordinateResponse> coordinates;
}
