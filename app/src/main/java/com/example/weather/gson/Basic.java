package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weather_id;

    @SerializedName("lon")
    public String longitude;

    @SerializedName("lat")
    public String latitude;

    public Basic(String cityName, String weather_id, String longitude, String latitude) {
        this.cityName = cityName;
        this.weather_id = weather_id;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
