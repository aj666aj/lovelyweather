package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    public String status;

    public String wind_dir;

    public String pres;

    public String temperature;

    public String info;

    public Now(String status, String wind_dir, String pres, String temperature, String info) {
        this.status = status;
        this.wind_dir = wind_dir;
        this.pres = pres;
        this.temperature = temperature;
        this.info = info;
    }

    @Override
    public String toString() {
        return "Now{" +
                "status='" + status + '\'' +
                ", wind_dir='" + wind_dir + '\'' +
                ", pres='" + pres + '\'' +
                ", temperature='" + temperature + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
