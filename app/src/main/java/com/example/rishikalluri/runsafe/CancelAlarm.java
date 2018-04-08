package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rishi Kalluri on 3/9/2018.
 */

public class CancelAlarm {

    @SerializedName("status")
    private String status;

    public CancelAlarm(String status) {
        this.status = status;
    }
}
