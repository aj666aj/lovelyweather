package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    public String status;

    public Basic basic;


    public Now now;

    @SerializedName("lifestyle")
    public List<Suggestion> suggestions;

    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;

    public Weather(String status, Basic basic, Now now, List<Suggestion> suggestions, List<Forecast> forecasts) {
        this.status = status;
        this.basic = basic;
        this.now = now;
        this.suggestions = suggestions;
        this.forecasts = forecasts;
    }
}
