package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.db.County;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Now;
import com.example.weather.gson.Suggestion;
import com.example.weather.gson.Weather;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.prefs.PreferenceChangeEvent;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView windDirText;

    private TextView presText;

    private TextView drsgText;

    private TextView fluText;

    private TextView airText;

    private ImageView bing_pic;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 21){
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_weather);

        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        windDirText = findViewById(R.id.wind_dir_txt);
        presText = findViewById(R.id.pres_txt);
        drsgText = findViewById(R.id.drsg_text);
        fluText = findViewById(R.id.flu_text);
        airText = findViewById(R.id.air_text);
        bing_pic = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nowStr = prefs.getString("now",null);
        String suggestionStr = prefs.getString("suggestions",null);
        String weatherString = prefs.getString("weather",null);
        if (nowStr != null && suggestionStr != null && weatherString != null){
            Now now = Utility.handleNowWeatherResponse(nowStr);
            List<Suggestion> suggestions = Utility.handleSuggestionResponse(suggestionStr);
            Weather weather = Utility.handleWeatherResponse(weatherString, now, suggestions);
            mWeatherId = weather.basic.weather_id;
            showWeatherInfo(weather);
        }else{
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestNowWeather(mWeatherId);
            requestSuggestions(mWeatherId);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBingPic();
                requestNowWeather(mWeatherId);
                requestSuggestions(mWeatherId);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                requestWeather(mWeatherId);
            }
        });
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bing_pic);
        }else{
            Log.d("WeatherActivity", "onCreate: "+"null");
            loadBingPic();
        }
    }



    public void requestNowWeather(final String weatherId){

        String nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        HttpUtil.sendOkHttpRequest(nowWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取当前天气信息失败1", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseText != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("now", responseText);
                            editor.apply();
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取当前天气信息失败2",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void requestSuggestions(String weatherId){

        String lifeStyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        HttpUtil.sendOkHttpRequest(lifeStyleUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取建议信息失败1", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseText != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("suggestions", responseText);
                            editor.apply();
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取建议信息失败2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void requestWeather(final String weatherId){

        String foreCastUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        HttpUtil.sendOkHttpRequest(foreCastUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取未来天气信息失败1", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                        String nowStr = prefs.getString("now",null);
                        String suggestionStr = prefs.getString("suggestions",null);
                        Now now = Utility.handleNowWeatherResponse(nowStr);
                        List<Suggestion> suggestions = Utility.handleSuggestionResponse(suggestionStr);
                        if (now != null && suggestions != null){
                            Weather weather = Utility.handleWeatherResponse(responseText, now, suggestions);
                            if (weather != null && weather.status.equals("ok")){
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                                mWeatherId = weather.basic.weather_id;
                                showWeatherInfo(weather);
                            }
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取未来天气信息失败2", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.now.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond_txt_d);
            maxText.setText(forecast.tmp_max + "℃");
            minText.setText(forecast.tmp_min + "℃");
            forecastLayout.addView(view);
        }
        windDirText.setText(weather.now.wind_dir);
        presText.setText(weather.now.pres);
        for (Suggestion suggestion : weather.suggestions){
            if(suggestion.type.equals("drsg")){
                drsgText.setText("穿着建议：" + suggestion.txt);
            }else if (suggestion.type.equals("flu")){
                fluText.setText("感冒指数：" + suggestion.txt);
            }else if (suggestion.type.equals("air")){
                airText.setText("空气质量：" + suggestion.txt);
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public void loadBingPic(){
        final String requestBingPic = "http://182.92.217.213/bing_pic.html";
        Log.d("WeatherActivity", "onResponse: "+requestBingPic);
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "请求背景图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("WeatherActivity", "onResponse: "+bingPic);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic", bingPic);
                        editor.apply();
                        Glide.with(WeatherActivity.this).load(bingPic).into(bing_pic);
                    }
                });
            }
        });
    }

}
