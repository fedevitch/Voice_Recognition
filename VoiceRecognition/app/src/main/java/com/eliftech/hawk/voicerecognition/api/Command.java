package com.eliftech.hawk.voicerecognition.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hawk on 08.07.16.
 */

public class Command{

    @SerializedName("text")
    final String text;

    public Command(String text){
        this.text = text;
    }

}