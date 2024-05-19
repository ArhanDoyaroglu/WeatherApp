package com.cs310.weatherapp;


import android.app.AlarmManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.TextView;

import android.widget.ToggleButton;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;







import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView cityNameTV, temperatureTv, conditionTV, alertTitle, alertMessage;
    private RecyclerView weatherRv;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private Date date;
    private int days;
    private ArrayList<WeatherRV> weatherArrayList;
    private WeatherRVAdapter weatherAdapter;
    private Button btnCelsiusToFahrenheit, AlertButton;
    private boolean isC = true;
    private String uv, humidity, visibility, userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userEmail")) {
            String userEmail = intent.getStringExtra("userEmail");
            if (userEmail != null) {
                int atIndex = userEmail.indexOf('@');
                if (atIndex != -1) { // Check if '@' character exists in the email
                    userName = userEmail.substring(0, atIndex);
                }
            }
        }

        cityNameTV = findViewById(R.id.idCityName);
        temperatureTv = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRv = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idBack);
        iconIV = findViewById(R.id.idTVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherArrayList = new ArrayList<>();
        weatherAdapter = new WeatherRVAdapter(this, weatherArrayList);
        weatherRv.setAdapter(weatherAdapter);
        AlertButton = findViewById(R.id.idAlertsButton);
        btnCelsiusToFahrenheit = findViewById(R.id.btnCelsiusToFahrenheit);



        AlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
                alertTitle = dialogView.findViewById(R.id.alertTitle);
                alertMessage = dialogView.findViewById(R.id.alertMessage);



                TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
                TextView alertMessage = dialogView.findViewById(R.id.alertMessage);
                String condition = conditionTV.getText().toString();
                String cityName = cityNameTV.getText().toString();
                String temp = temperatureTv.getText().toString();
                alertTitle.setText(String.format("Welcome, %s!", userName));
                if(isC)
                {
                    alertMessage.setText(String.format("Currently, in%s, the weather is %s with a temperature of %s.The UV index stands at %s.The humidity is relatively low at %s%%. Visibility is %s km.", cityName, condition, temp, uv, humidity, visibility));
                }
                else
                {
                    alertMessage.setText(String.format("Currently, in%s, the weather is %s with a temperature of %s.The UV index stands at %s.The humidity is relatively low at %s%%. Visibility is %s miles.", cityName, condition, temp, uv, humidity, visibility));
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView);


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        getWeather("Istanbul", isC);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty() || weatherData(city).isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please enter valid city");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                else
                {
                    getWeather(city, isC);
                }
            }
        });
        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnCelsiusToFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isC = !isC;
                String city = (String)cityNameTV.getText();
                getWeather(city , isC);
            }
        });


    }

    private String weatherDataHourly(String cityName, Date date, int days)
    {
        return "{\"location\":{\"name\":\"Paris\",\"region\":\"Ile-de-France\",\"country\":\"France\",\"lat\":48.87,\"lon\":2.33,\"tz_id\":\"Europe/Paris\",\"localtime_epoch\":1714054315,\"localtime\":\"2024-04-25 16:11\"},\"current\":{\"last_updated_epoch\":1714053600,\"last_updated\":\"2024-04-25 16:00\",\"temp_c\":12.0,\"temp_f\":53.6,\"is_day\":1,\"condition\":{\"text\":\"Light rain\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/296.png\",\"code\":1183},\"wind_mph\":8.1,\"wind_kph\":13.0,\"wind_degree\":230,\"wind_dir\":\"SW\",\"pressure_mb\":1006.0,\"pressure_in\":29.71,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":35,\"cloud\":37,\"feelslike_c\":10.5,\"feelslike_f\":50.8,\"vis_km\":10.0,\"vis_miles\":6.0,\"uv\":4.0,\"gust_mph\":10.3,\"gust_kph\":16.6},\"forecast\":{\"forecastday\":[{\"date\":\"2024-04-26\",\"date_epoch\":1714089600,\"day\":{\"maxtemp_c\":15.0,\"maxtemp_f\":59.0,\"mintemp_c\":5.6,\"mintemp_f\":42.1,\"avgtemp_c\":10.4,\"avgtemp_f\":50.7,\"maxwind_mph\":7.6,\"maxwind_kph\":12.2,\"totalprecip_mm\":1.25,\"totalprecip_in\":0.05,\"totalsnow_cm\":0.0,\"avgvis_km\":9.7,\"avgvis_miles\":6.0,\"avghumidity\":70,\"daily_will_it_rain\":1,\"daily_chance_of_rain\":88,\"daily_will_it_snow\":0,\"daily_chance_of_snow\":0,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"uv\":3.0},\"astro\":{\"sunrise\":\"06:38 AM\",\"sunset\":\"08:59 PM\",\"moonrise\":\"No moonrise\",\"moonset\":\"07:23 AM\",\"moon_phase\":\"Waning Gibbous\",\"moon_illumination\":96,\"is_moon_up\":1,\"is_sun_up\":0},\"hour\":[{\"time_epoch\":1714082400,\"time\":\"2024-04-26 00:00\",\"temp_c\":7.3,\"temp_f\":45.1,\"is_day\":0,\"condition\":{\"text\":\"Clear \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/113.png\",\"code\":1000},\"wind_mph\":4.5,\"wind_kph\":7.2,\"wind_degree\":202,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":83,\"cloud\":21,\"feelslike_c\":6.0,\"feelslike_f\":42.8,\"windchill_c\":6.0,\"windchill_f\":42.8,\"heatindex_c\":7.3,\"heatindex_f\":45.1,\"dewpoint_c\":4.6,\"dewpoint_f\":40.3,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.1,\"gust_kph\":13.0,\"uv\":1.0},{\"time_epoch\":1714086000,\"time\":\"2024-04-26 01:00\",\"temp_c\":6.5,\"temp_f\":43.8,\"is_day\":0,\"condition\":{\"text\":\"Clear \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/113.png\",\"code\":1000},\"wind_mph\":4.3,\"wind_kph\":6.8,\"wind_degree\":204,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":85,\"cloud\":23,\"feelslike_c\":5.3,\"feelslike_f\":41.5,\"windchill_c\":5.3,\"windchill_f\":41.5,\"heatindex_c\":6.5,\"heatindex_f\":43.8,\"dewpoint_c\":4.2,\"dewpoint_f\":39.5,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.0,\"gust_kph\":12.9,\"uv\":1.0},{\"time_epoch\":1714089600,\"time\":\"2024-04-26 02:00\",\"temp_c\":6.1,\"temp_f\":43.0,\"is_day\":0,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/116.png\",\"code\":1003},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":203,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":86,\"cloud\":27,\"feelslike_c\":5.0,\"feelslike_f\":41.0,\"windchill_c\":5.0,\"windchill_f\":41.0,\"heatindex_c\":6.1,\"heatindex_f\":43.0,\"dewpoint_c\":3.9,\"dewpoint_f\":39.1,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.2,\"gust_kph\":11.6,\"uv\":1.0},{\"time_epoch\":1714093200,\"time\":\"2024-04-26 03:00\",\"temp_c\":5.9,\"temp_f\":42.7,\"is_day\":0,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/116.png\",\"code\":1003},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":199,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.64,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":86,\"cloud\":29,\"feelslike_c\":4.8,\"feelslike_f\":40.6,\"windchill_c\":4.8,\"windchill_f\":40.6,\"heatindex_c\":5.9,\"heatindex_f\":42.7,\"dewpoint_c\":3.8,\"dewpoint_f\":38.9,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.2,\"gust_kph\":11.5,\"uv\":1.0},{\"time_epoch\":1714096800,\"time\":\"2024-04-26 04:00\",\"temp_c\":5.8,\"temp_f\":42.5,\"is_day\":0,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/116.png\",\"code\":1003},\"wind_mph\":3.6,\"wind_kph\":5.8,\"wind_degree\":204,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.64,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":86,\"cloud\":44,\"feelslike_c\":4.8,\"feelslike_f\":40.6,\"windchill_c\":4.8,\"windchill_f\":40.6,\"heatindex_c\":5.8,\"heatindex_f\":42.5,\"dewpoint_c\":3.7,\"dewpoint_f\":38.7,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":6.6,\"gust_kph\":10.7,\"uv\":1.0},{\"time_epoch\":1714100400,\"time\":\"2024-04-26 05:00\",\"temp_c\":5.6,\"temp_f\":42.1,\"is_day\":0,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/116.png\",\"code\":1003},\"wind_mph\":3.6,\"wind_kph\":5.8,\"wind_degree\":203,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.63,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":86,\"cloud\":47,\"feelslike_c\":4.5,\"feelslike_f\":40.1,\"windchill_c\":4.5,\"windchill_f\":40.1,\"heatindex_c\":5.6,\"heatindex_f\":42.1,\"dewpoint_c\":3.4,\"dewpoint_f\":38.2,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":6.7,\"gust_kph\":10.8,\"uv\":1.0},{\"time_epoch\":1714104000,\"time\":\"2024-04-26 06:00\",\"temp_c\":5.6,\"temp_f\":42.1,\"is_day\":0,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/116.png\",\"code\":1003},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":199,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.64,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":85,\"cloud\":60,\"feelslike_c\":4.4,\"feelslike_f\":39.9,\"windchill_c\":4.4,\"windchill_f\":39.9,\"heatindex_c\":5.6,\"heatindex_f\":42.1,\"dewpoint_c\":3.4,\"dewpoint_f\":38.0,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.0,\"gust_kph\":11.2,\"uv\":1.0},{\"time_epoch\":1714107600,\"time\":\"2024-04-26 07:00\",\"temp_c\":6.9,\"temp_f\":44.4,\"is_day\":1,\"condition\":{\"text\":\"Partly Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/116.png\",\"code\":1003},\"wind_mph\":4.3,\"wind_kph\":6.8,\"wind_degree\":194,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":81,\"cloud\":60,\"feelslike_c\":5.7,\"feelslike_f\":42.2,\"windchill_c\":5.7,\"windchill_f\":42.2,\"heatindex_c\":6.9,\"heatindex_f\":44.5,\"dewpoint_c\":4.0,\"dewpoint_f\":39.1,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":6.4,\"gust_kph\":10.3,\"uv\":3.0},{\"time_epoch\":1714111200,\"time\":\"2024-04-26 08:00\",\"temp_c\":8.4,\"temp_f\":47.2,\"is_day\":1,\"condition\":{\"text\":\"Cloudy \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/119.png\",\"code\":1006},\"wind_mph\":5.8,\"wind_kph\":9.4,\"wind_degree\":200,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.66,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":76,\"cloud\":67,\"feelslike_c\":6.9,\"feelslike_f\":44.4,\"windchill_c\":6.9,\"windchill_f\":44.4,\"heatindex_c\":8.4,\"heatindex_f\":47.2,\"dewpoint_c\":4.4,\"dewpoint_f\":40.0,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.4,\"gust_kph\":11.8,\"uv\":2.0},{\"time_epoch\":1714114800,\"time\":\"2024-04-26 09:00\",\"temp_c\":9.9,\"temp_f\":49.8,\"is_day\":1,\"condition\":{\"text\":\"Overcast \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/122.png\",\"code\":1009},\"wind_mph\":6.5,\"wind_kph\":10.4,\"wind_degree\":203,\"wind_dir\":\"SSW\",\"pressure_mb\":1005.0,\"pressure_in\":29.66,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":71,\"cloud\":100,\"feelslike_c\":8.4,\"feelslike_f\":47.2,\"windchill_c\":8.4,\"windchill_f\":47.2,\"heatindex_c\":9.9,\"heatindex_f\":49.8,\"dewpoint_c\":5.0,\"dewpoint_f\":40.9,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.7,\"gust_kph\":12.3,\"uv\":2.0},{\"time_epoch\":1714118400,\"time\":\"2024-04-26 10:00\",\"temp_c\":11.5,\"temp_f\":52.6,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":7.2,\"wind_kph\":11.5,\"wind_degree\":208,\"wind_dir\":\"SSW\",\"pressure_mb\":1005.0,\"pressure_in\":29.67,\"precip_mm\":0.01,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":66,\"cloud\":86,\"feelslike_c\":10.2,\"feelslike_f\":50.3,\"windchill_c\":10.2,\"windchill_f\":50.3,\"heatindex_c\":11.5,\"heatindex_f\":52.6,\"dewpoint_c\":5.4,\"dewpoint_f\":41.8,\"will_it_rain\":0,\"chance_of_rain\":70,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.2,\"gust_kph\":13.2,\"uv\":3.0},{\"time_epoch\":1714122000,\"time\":\"2024-04-26 11:00\",\"temp_c\":12.8,\"temp_f\":55.0,\"is_day\":1,\"condition\":{\"text\":\"Light drizzle\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/266.png\",\"code\":1153},\"wind_mph\":7.4,\"wind_kph\":11.9,\"wind_degree\":205,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.66,\"precip_mm\":0.23,\"precip_in\":0.01,\"snow_cm\":0.0,\"humidity\":62,\"cloud\":80,\"feelslike_c\":11.7,\"feelslike_f\":53.0,\"windchill_c\":11.7,\"windchill_f\":53.0,\"heatindex_c\":12.8,\"heatindex_f\":55.0,\"dewpoint_c\":5.7,\"dewpoint_f\":42.2,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":2.0,\"vis_miles\":1.0,\"gust_mph\":8.5,\"gust_kph\":13.7,\"uv\":3.0},{\"time_epoch\":1714125600,\"time\":\"2024-04-26 12:00\",\"temp_c\":13.4,\"temp_f\":56.1,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":7.6,\"wind_kph\":12.2,\"wind_degree\":202,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.35,\"precip_in\":0.01,\"snow_cm\":0.0,\"humidity\":60,\"cloud\":100,\"feelslike_c\":12.4,\"feelslike_f\":54.3,\"windchill_c\":12.4,\"windchill_f\":54.3,\"heatindex_c\":13.4,\"heatindex_f\":56.1,\"dewpoint_c\":5.7,\"dewpoint_f\":42.3,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":9.0,\"gust_kph\":14.5,\"uv\":3.0},{\"time_epoch\":1714129200,\"time\":\"2024-04-26 13:00\",\"temp_c\":14.0,\"temp_f\":57.2,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":7.4,\"wind_kph\":11.9,\"wind_degree\":199,\"wind_dir\":\"SSW\",\"pressure_mb\":1004.0,\"pressure_in\":29.64,\"precip_mm\":0.16,\"precip_in\":0.01,\"snow_cm\":0.0,\"humidity\":57,\"cloud\":100,\"feelslike_c\":13.2,\"feelslike_f\":55.7,\"windchill_c\":13.2,\"windchill_f\":55.7,\"heatindex_c\":14.0,\"heatindex_f\":57.2,\"dewpoint_c\":5.6,\"dewpoint_f\":42.0,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.5,\"gust_kph\":13.7,\"uv\":3.0},{\"time_epoch\":1714132800,\"time\":\"2024-04-26 14:00\",\"temp_c\":14.9,\"temp_f\":58.9,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":7.4,\"wind_kph\":11.9,\"wind_degree\":197,\"wind_dir\":\"SSW\",\"pressure_mb\":1003.0,\"pressure_in\":29.63,\"precip_mm\":0.06,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":52,\"cloud\":100,\"feelslike_c\":14.3,\"feelslike_f\":57.7,\"windchill_c\":14.3,\"windchill_f\":57.7,\"heatindex_c\":14.9,\"heatindex_f\":58.9,\"dewpoint_c\":5.1,\"dewpoint_f\":41.2,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.5,\"gust_kph\":13.7,\"uv\":3.0},{\"time_epoch\":1714136400,\"time\":\"2024-04-26 15:00\",\"temp_c\":14.8,\"temp_f\":58.6,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":7.6,\"wind_kph\":12.2,\"wind_degree\":195,\"wind_dir\":\"SSW\",\"pressure_mb\":1003.0,\"pressure_in\":29.62,\"precip_mm\":0.05,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":51,\"cloud\":61,\"feelslike_c\":14.1,\"feelslike_f\":57.3,\"windchill_c\":14.1,\"windchill_f\":57.3,\"heatindex_c\":14.8,\"heatindex_f\":58.6,\"dewpoint_c\":4.8,\"dewpoint_f\":40.6,\"will_it_rain\":0,\"chance_of_rain\":70,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":9.2,\"gust_kph\":14.8,\"uv\":3.0},{\"time_epoch\":1714140000,\"time\":\"2024-04-26 16:00\",\"temp_c\":15.0,\"temp_f\":59.0,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":7.6,\"wind_kph\":12.2,\"wind_degree\":191,\"wind_dir\":\"SSW\",\"pressure_mb\":1003.0,\"pressure_in\":29.61,\"precip_mm\":0.03,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":50,\"cloud\":100,\"feelslike_c\":14.3,\"feelslike_f\":57.8,\"windchill_c\":14.3,\"windchill_f\":57.8,\"heatindex_c\":15.0,\"heatindex_f\":59.0,\"dewpoint_c\":4.6,\"dewpoint_f\":40.3,\"will_it_rain\":1,\"chance_of_rain\":78,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":9.4,\"gust_kph\":15.0,\"uv\":3.0},{\"time_epoch\":1714143600,\"time\":\"2024-04-26 17:00\",\"temp_c\":14.6,\"temp_f\":58.2,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":6.9,\"wind_kph\":11.2,\"wind_degree\":191,\"wind_dir\":\"SSW\",\"pressure_mb\":1003.0,\"pressure_in\":29.61,\"precip_mm\":0.06,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":52,\"cloud\":100,\"feelslike_c\":13.9,\"feelslike_f\":57.1,\"windchill_c\":13.9,\"windchill_f\":57.1,\"heatindex_c\":14.6,\"heatindex_f\":58.2,\"dewpoint_c\":4.8,\"dewpoint_f\":40.6,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":9.2,\"gust_kph\":14.9,\"uv\":3.0},{\"time_epoch\":1714147200,\"time\":\"2024-04-26 18:00\",\"temp_c\":14.1,\"temp_f\":57.3,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":6.0,\"wind_kph\":9.7,\"wind_degree\":192,\"wind_dir\":\"SSW\",\"pressure_mb\":1003.0,\"pressure_in\":29.61,\"precip_mm\":0.05,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":55,\"cloud\":88,\"feelslike_c\":13.5,\"feelslike_f\":56.4,\"windchill_c\":13.5,\"windchill_f\":56.4,\"heatindex_c\":14.1,\"heatindex_f\":57.3,\"dewpoint_c\":5.2,\"dewpoint_f\":41.4,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.5,\"gust_kph\":13.6,\"uv\":3.0},{\"time_epoch\":1714150800,\"time\":\"2024-04-26 19:00\",\"temp_c\":13.0,\"temp_f\":55.4,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":4.7,\"wind_kph\":7.6,\"wind_degree\":190,\"wind_dir\":\"S\",\"pressure_mb\":1003.0,\"pressure_in\":29.62,\"precip_mm\":0.02,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":61,\"cloud\":62,\"feelslike_c\":12.6,\"feelslike_f\":54.8,\"windchill_c\":12.6,\"windchill_f\":54.8,\"heatindex_c\":13.0,\"heatindex_f\":55.4,\"dewpoint_c\":5.7,\"dewpoint_f\":42.3,\"will_it_rain\":1,\"chance_of_rain\":77,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.2,\"gust_kph\":11.6,\"uv\":3.0},{\"time_epoch\":1714154400,\"time\":\"2024-04-26 20:00\",\"temp_c\":11.5,\"temp_f\":52.7,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":4.3,\"wind_kph\":6.8,\"wind_degree\":168,\"wind_dir\":\"SSE\",\"pressure_mb\":1004.0,\"pressure_in\":29.64,\"precip_mm\":0.02,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":67,\"cloud\":77,\"feelslike_c\":11.0,\"feelslike_f\":51.8,\"windchill_c\":11.0,\"windchill_f\":51.8,\"heatindex_c\":11.5,\"heatindex_f\":52.7,\"dewpoint_c\":5.7,\"dewpoint_f\":42.2,\"will_it_rain\":0,\"chance_of_rain\":62,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.8,\"gust_kph\":12.5,\"uv\":3.0},{\"time_epoch\":1714158000,\"time\":\"2024-04-26 21:00\",\"temp_c\":10.9,\"temp_f\":51.7,\"is_day\":0,\"condition\":{\"text\":\"Overcast \",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/122.png\",\"code\":1009},\"wind_mph\":4.7,\"wind_kph\":7.6,\"wind_degree\":153,\"wind_dir\":\"SSE\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.0,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":71,\"cloud\":100,\"feelslike_c\":10.2,\"feelslike_f\":50.3,\"windchill_c\":10.2,\"windchill_f\":50.3,\"heatindex_c\":10.9,\"heatindex_f\":51.7,\"dewpoint_c\":6.0,\"dewpoint_f\":42.8,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.8,\"gust_kph\":14.2,\"uv\":1.0},{\"time_epoch\":1714161600,\"time\":\"2024-04-26 22:00\",\"temp_c\":10.5,\"temp_f\":50.9,\"is_day\":0,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/176.png\",\"code\":1063},\"wind_mph\":4.5,\"wind_kph\":7.2,\"wind_degree\":153,\"wind_dir\":\"SSE\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.1,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":76,\"cloud\":100,\"feelslike_c\":9.8,\"feelslike_f\":49.6,\"windchill_c\":9.8,\"windchill_f\":49.6,\"heatindex_c\":10.5,\"heatindex_f\":50.9,\"dewpoint_c\":6.5,\"dewpoint_f\":43.8,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.2,\"gust_kph\":13.2,\"uv\":1.0},{\"time_epoch\":1714165200,\"time\":\"2024-04-26 23:00\",\"temp_c\":9.9,\"temp_f\":49.8,\"is_day\":0,\"condition\":{\"text\":\"Patchy rain nearby\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/176.png\",\"code\":1063},\"wind_mph\":4.5,\"wind_kph\":7.2,\"wind_degree\":170,\"wind_dir\":\"S\",\"pressure_mb\":1004.0,\"pressure_in\":29.65,\"precip_mm\":0.12,\"precip_in\":0.0,\"snow_cm\":0.0,\"humidity\":84,\"cloud\":78,\"feelslike_c\":9.1,\"feelslike_f\":48.3,\"windchill_c\":9.1,\"windchill_f\":48.3,\"heatindex_c\":9.9,\"heatindex_f\":49.8,\"dewpoint_c\":7.3,\"dewpoint_f\":45.1,\"will_it_rain\":1,\"chance_of_rain\":100,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.9,\"gust_kph\":12.7,\"uv\":1.0}]}]}}\n";
    }
    private String weatherData(String cityName)
    {
        return "{\"location\":{\"name\":\"Paris\",\"region\":\"Ile-de-France\",\"country\":\"France\",\"lat\":48.87,\"lon\":2.33,\"tz_id\":\"Europe/Paris\",\"localtime_epoch\":1714054310,\"localtime\":\"2024-04-25 16:11\"},\"current\":{\"last_updated_epoch\":1714053600,\"last_updated\":\"2024-04-25 16:00\",\"temp_c\":12.0,\"temp_f\":53.6,\"is_day\":1,\"condition\":{\"text\":\"Sunny\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/113.png\",\"code\":1000},\"wind_mph\":11.9,\"wind_kph\":19.1,\"wind_degree\":240,\"wind_dir\":\"WSW\",\"pressure_mb\":1006.0,\"pressure_in\":29.71,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":44,\"cloud\":0,\"feelslike_c\":10.5,\"feelslike_f\":50.8,\"vis_km\":10.0,\"vis_miles\":6.0,\"uv\":4.0,\"gust_mph\":16.3,\"gust_kph\":26.3}}";
    }

    private void getWeather(String cityName, boolean isC)
    {

         cityNameTV.setText(cityName);
         String jsonResponseHourly = weatherDataHourly(cityName, date, days);
         String jsonResponseDaily = weatherData(cityName);
         weatherArrayList.clear();


        try
                    {
                        JSONObject forecastDataCurr = new JSONObject(jsonResponseDaily);
                        if(isC)
                        {
                            String temp = forecastDataCurr.getJSONObject("current").getString("temp_c")+"°C";
                            temperatureTv.setText(temp);
                            visibility = forecastDataCurr.getJSONObject("current").getString("vis_km");

                        }
                        else
                        {
                            String temp = forecastDataCurr.getJSONObject("current").getString("temp_f")+"°F";
                            temperatureTv.setText(temp);
                            visibility = forecastDataCurr.getJSONObject("current").getString("vis_miles");
                        }

                        int isDay = forecastDataCurr.getJSONObject("current").getInt("is_day");
                        String condition = forecastDataCurr.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditionIcon = forecastDataCurr.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("https:".concat(conditionIcon)).resize(256, 256).placeholder(R.drawable.ic_launcher_foreground).into(iconIV);
                        conditionTV.setText(condition);
                        uv = forecastDataCurr.getJSONObject("current").getString("uv");
                        humidity = forecastDataCurr.getJSONObject("current").getString("humidity");
                        if(isDay==1)
                        {
                            Picasso.get().load("https://i.pinimg.com/originals/b5/6f/cb/b56fcb549c41f064f8921ef52c44e3ca.jpg").into(backIV);
                        }
                        else
                        {
                            Picasso.get().load("https://i.pinimg.com/originals/27/7a/16/277a1617605e228aae64ace30d8fe801.jpg").into(backIV);
                        }
                        JSONObject forecastData = new JSONObject(jsonResponseHourly);
                        JSONArray forecastDays = forecastData.getJSONObject("forecast").getJSONArray("forecastday");
                        JSONArray hourArray = forecastDays.getJSONObject(0).getJSONArray("hour");

                        for(int i=0; i<hourArray.length(); i++)
                        {
                            String temperature, wind;
                            JSONObject hourObj = hourArray.getJSONObject(i);
                            String time = hourObj.getString("time");
                            if(isC)
                            {
                                temperature = hourObj.getString("temp_c");
                                wind = hourObj.getString("wind_kph");
                            }
                            else
                            {
                               temperature = hourObj.getString("temp_f");
                               wind = hourObj.getString("wind_mph");
                            }
                            String conditionImg = hourObj.getJSONObject("condition").getString("icon");

                            weatherArrayList.add(new WeatherRV(time, temperature, conditionImg, wind, isC));
                        }
                        weatherAdapter.notifyDataSetChanged();
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }

             }
   }

