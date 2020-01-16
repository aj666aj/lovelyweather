package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    public String status;

    public String wind_dir;

    public String pres;

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String info;

}
