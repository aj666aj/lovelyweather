package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    public String status;

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String info;

}
