package com.example.rishikalluri.runsafe;
//package io.futurestud.retrofit18.api.service;

import android.util.Base64;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;



import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Url;


/**
 * Created by Rishi Kalluri on 3/6/2018.
 */

public interface Client {

    @POST("https://login-sandbox.safetrek.io/oauth/token")
    Call<AccessToken> getAccessToken(
            @HeaderMap Map<String, String> headers,
            @Body Map<String, String> body);



    @POST("https://api-sandbox.safetrek.io/v1/alarms/")
    Call<Alarm> sendAlarm (
            @HeaderMap Map<String, String> headers,
            @Body AlarmRequest body);


    @POST
    Call<Alarm> cancelAlarm (
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @Body CancelAlarm body);





}
