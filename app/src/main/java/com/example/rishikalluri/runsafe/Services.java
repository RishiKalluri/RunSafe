package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/7/2018.
 */

public class Services {

    @SerializedName("police")
    private boolean police;

    @SerializedName("fire")
    private boolean fire;

    @SerializedName("medical")
    private boolean medical;

    public Services(boolean police, boolean fire, boolean medical) {
        this.police = police;
        this.fire = fire;
        this.medical = medical;
    }
}
