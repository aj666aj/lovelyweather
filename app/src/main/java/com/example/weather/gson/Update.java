package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Update {

    @SerializedName("loc")
    public String updateTime;

    public Update(String updateTime) {
        this.updateTime = updateTime;
    }
}
