package com.eliftech.hawk.voicerecognition.api;

/**
 * Created by hawk on 08.07.16.
 */

import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;


public interface APIService {

    @Headers({
        "Content-Type: application/json"
    })

    @POST("/")
    void sendCommand(@Body Command body, retrofit.Callback<ServerResponse> response);
}