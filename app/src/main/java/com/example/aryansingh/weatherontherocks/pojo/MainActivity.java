package com.example.aryansingh.weatherontherocks.pojo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryansingh.weatherontherocks.Currently;
import com.example.aryansingh.weatherontherocks.Daily;
import com.example.aryansingh.weatherontherocks.Datum;
import com.example.aryansingh.weatherontherocks.Hourly;
import com.example.aryansingh.weatherontherocks.R;
import com.example.aryansingh.weatherontherocks.WeatherDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity{

    List<Datum> hourDetailsList;
    WeatherAdaptor weatherAdaptor;
    RecyclerView hourlyRecyclerView;

    FusedLocationProviderClient client;
    LocationCallback callback;
    LocationRequest request;

    List<Datum> dailyDetailsList;
    DailyWeatherAdaptor dailyweatherAdaptor;
    RecyclerView dailyRecyclerView;

    float currentLatitude;// = 37.8267f;
    float currentLongitude;// = -122.4233f;

    TextView temperature_current, range, feel, location, datetime,
            current_summary, wind, uv, moon, humidity;
    ImageView image_weather;
    Retrofit retrofit = WeatherRetrofitClient.getClient();
    WeatherInterface weatherInterface = retrofit.create(WeatherInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = findViewById(R.id.location);
        image_weather = findViewById(R.id.image_weather);
        datetime = findViewById(R.id.datetime);
        temperature_current = findViewById(R.id.temperature_current);
        range = findViewById(R.id.range);
        feel = findViewById(R.id.feel);

        wind = findViewById(R.id.wind);
        uv = findViewById(R.id.uv);
        moon = findViewById(R.id.moon);
        humidity = findViewById(R.id.humidity);

        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        dailyRecyclerView = findViewById(R.id.dailyRecyclerView);
        current_summary = findViewById(R.id.current_summary);
        final Calendar calendar = Calendar.getInstance();




        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    currentLongitude = (float) location.getLongitude();
                    currentLatitude = (float) location.getLatitude();
                    invokeCall();
                }

            }
        });
        request = LocationRequest.create();
        request.setInterval(3000);
        request.setFastestInterval(2000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

         callback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locations = locationResult.getLocations();
                if(locations != null && locations.size() > 0){
                    Location location = locations.get(0);
                    Toast.makeText(MainActivity.this,location.getLatitude() + " : " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                }
            }
        };

        dailyDetailsList = new ArrayList<>();
        dailyweatherAdaptor = new DailyWeatherAdaptor(this, dailyDetailsList, new DailyWeatherAdaptor.MoviesClickListener() {
            @Override
            public void onMovieClick(View view, int position) {

            }
        });
        dailyRecyclerView.setAdapter(dailyweatherAdaptor);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dailyRecyclerView.setLayoutManager(linearLayoutManager1);

        hourDetailsList = new ArrayList<>();
        weatherAdaptor = new WeatherAdaptor(this, hourDetailsList, new WeatherAdaptor.MoviesClickListener() {
            @Override
            public void onMovieClick(View view, int position) {

            }
        });
        hourlyRecyclerView.setAdapter(weatherAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void invokeCall(){
        Call<WeatherDetails> getDaily = weatherInterface.getWeatherDetails(WeatherConstants.WEATHER_API, currentLatitude, currentLongitude);
        getDaily.enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(Call<WeatherDetails> call, Response<WeatherDetails> response) {
                WeatherDetails weatherDetails = response.body();
                Daily daily = weatherDetails.getDaily();

                List<Datum> dailyList = daily.getData();
                dailyDetailsList.clear();
                dailyDetailsList.addAll(dailyList);
                dailyweatherAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<WeatherDetails> call, Throwable t) {

            }
        });

        Call<WeatherDetails> getCurrent = weatherInterface.getWeatherDetails(WeatherConstants.WEATHER_API, currentLatitude, currentLongitude);
        getCurrent.enqueue(new Callback<WeatherDetails>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<WeatherDetails> call, Response<WeatherDetails> response) {
                WeatherDetails weatherDetails = response.body();
                Daily daily = weatherDetails.getDaily();
                Currently currently = weatherDetails.getCurrently();
                List<Datum> dailyList = daily.getData();

                Date date = new Date(currently.getTime()*1000L);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                datetime.setText("Date: " + simpleDateFormat.format(date) + "  Time: " + simpleDateFormat1.format(date));
//
//                long time = currently.getTime();
//                datetime.setText(convert(time));

                current_summary.setText(dailyList.get(0).getSummary());
                temperature_current.setText(convertToC(currently.getTemperature()) + " \u2103");
                location.setText(weatherDetails.getTimezone());

                range.setText(convertToC(dailyList.get(0).getTemperatureMax()) + " \u2103" + " / " + convertToC(dailyList.get(0).getTemperatureMin()) + " \u2103");
                Double i =  ((currently.getApparentTemperature() + currently.getTemperature()) / 2);
                int s = convertToC(i);
                feel.setText("Feels like " + s + " \u2103");

                humidity.setText(currently.getHumidity() + "");
                uv.setText(currently.getUvIndex() + "");
                wind.setText(currently.getWindSpeed() + "");


                if(currently.getIcon().contains("partly-cloudy-day")){
                    image_weather.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                }
                else if(currently.getIcon().contains("clear-night")){
                    image_weather.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy_night));
                }
                else if(currently.getIcon().contains("clear-day")){
                    image_weather.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                }
                else
                    image_weather.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));


            }

            @Override
            public void onFailure(Call<WeatherDetails> call, Throwable t) {

            }
        });


        retrofit2.Call<WeatherDetails> getHourly = weatherInterface.getWeatherDetails(WeatherConstants.WEATHER_API, currentLatitude, currentLongitude);
        getHourly.enqueue(new Callback<WeatherDetails>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<WeatherDetails> call, Response<WeatherDetails> response) {
                WeatherDetails weatherDetails = response.body();
                Hourly hourly = weatherDetails.getHourly();

                List<Datum> list = hourly.getData();

                hourDetailsList.clear();
                hourDetailsList.addAll(list);
                weatherAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<WeatherDetails> call, Throwable t) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String convert(Long time){
        ZonedDateTime dateTime = Instant.ofEpochMilli(time)
                .atZone(ZoneId.of("Australia/Sydney"));
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return formatted;
    }

    public int convertToC(double temperature){
        temperature = (int)((temperature - 32)*5)/9;
        return (int)temperature;
    }
}
