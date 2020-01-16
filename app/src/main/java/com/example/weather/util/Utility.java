package com.example.weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weather.db.City;
import com.example.weather.db.County;
import com.example.weather.db.Province;
import com.example.weather.gson.Basic;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Now;
import com.example.weather.gson.Suggestion;
import com.example.weather.gson.Update;
import com.example.weather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Utility {

    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCIties = new JSONArray(response);
                for (int i=0;i<allCIties.length();i++){
                    JSONObject cityObject = allCIties.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(cityId);
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static Now handleNowWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String status = jsonArray.getJSONObject(0).getString("status");
            JSONObject nowObject = jsonArray.getJSONObject(0).getJSONObject("now");
            String wind_dir = nowObject.getString("wind_dir");
            String pres = nowObject.getString("pres");
            String temperature = nowObject.getString("tmp");
            String info = nowObject.getString("cond_txt");
            Now now = new Now(status,wind_dir,pres,temperature,info);
            return now;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Suggestion> handleSuggestionResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray middleArray = jsonObject.getJSONArray("HeWeather6");
            JSONObject middleObject = middleArray.getJSONObject(0);
            JSONArray jsonArray = middleObject.getJSONArray("lifestyle");
            List<Suggestion> suggestions = new ArrayList<>();
            for (int i = 0 ; i < jsonArray.length();i++){
                JSONObject suggestion = jsonArray.getJSONObject(i);
                String type = suggestion.getString("type");
                if (type.equals("drsg") || type.equals("flu") || type.equals("air")){
                    suggestions.add(new Suggestion(suggestion.getString("type"),suggestion.getString("brf"),suggestion.getString("txt")));
                }
            }
            return suggestions;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Weather handleWeatherResponse(String response, Now now, List<Suggestion> suggestions){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            JSONObject weatherObject = jsonArray.getJSONObject(0);
            JSONObject basicStr = weatherObject.getJSONObject("basic");
            JSONObject updateStr = weatherObject.getJSONObject("update");
            JSONArray forecastStr = weatherObject.getJSONArray("daily_forecast");
            String status = weatherObject.getString("status");
            Basic basic = new Basic(basicStr.getString("location"),basicStr.getString("cid"),basicStr.getString("lon"),basicStr.getString("lat"));
            Update update = new Update(updateStr.getString("loc"));
            List<Forecast> forecasts = new ArrayList<>();
            for (int i = 0; i < forecastStr.length(); i++){
                forecasts.add(new Gson().fromJson(forecastStr.getJSONObject(i).toString(),Forecast.class));
            }
            return new Weather(status,basic,update,now,suggestions,forecasts);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
