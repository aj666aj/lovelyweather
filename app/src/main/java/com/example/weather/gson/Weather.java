package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    public String status;

    public Basic basic;

    public Update update;

    @SerializedName("lifestyle")
    public List<Suggestion> suggestions;

    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;

}
