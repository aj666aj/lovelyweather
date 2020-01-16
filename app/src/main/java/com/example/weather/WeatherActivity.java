package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.gson.Now;
import com.example.weather.gson.Suggestion;
import com.example.weather.gson.Weather;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        airText = findViewById(R.id.pres_txt);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

    }

    public void requestWeather(final String weatherId){

        String nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        String lifeStyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        String foreCastUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" +
                weatherId +
                "&key=c8ea3381257e4754b22eda92c2503c18";

        HttpUtil.sendOkHttpRequest(nowWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Now now = Utility.handleNowWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (now != null && now.status.equals("OK")){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("now", responseText);
                            editor.apply();
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        

    }

}
