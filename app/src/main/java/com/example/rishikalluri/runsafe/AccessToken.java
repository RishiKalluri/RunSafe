package com.example.rishikalluri.runsafe;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Response;

/**
 * Created by Rishi Kalluri on 3/6/2018.
 */

class AccessToken {//} implements Interceptor{

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    /*
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization",
    }
    */

    public String getAccessToken() {

        return accessToken;
    }

    public String getTokenType() {

        return tokenType;
    }

    public AccessToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
