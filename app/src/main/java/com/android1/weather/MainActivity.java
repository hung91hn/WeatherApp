package com.android1.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android1.weather.adapter.DailyWeatherAdapter;
import com.android1.weather.adapter.HourlyWeatherAdapter;
import com.android1.weather.model.WeatherDaily;
import com.android1.weather.model.WeatherHourly;
import com.android1.weather.utils.CustomRequest;
import com.android1.weather.utils.JsonUtil;
import com.android1.weather.utils.MySingleton;
import com.android1.weather.utils.RLog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android1.weather.utils.JsonUtil.getInt;
import static com.android1.weather.utils.JsonUtil.getJSONObject;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "JVZgNJ8lEM4EpcoHyuQvEePb3HjPS6A4";
    private final String API_LOCATION = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
    private final String API_WEATHER_HOURLY = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/";
    private final String API_WEATHER_DAILY = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
    private final String URL_WEATHER_ICON = "http://developer.accuweather.com/sites/default/files/";
    private final String URL_WEATHER_ICON_TYPE = "-s.png";
    private final int FAKETEMP = 10000;
    private String unknow;
    private Resources resources;
    private GoogleApiClient mClient;
    private TextView tvCity, tvCountry, tvTimeZone, tvWeatherPhrase, tvDayLight, tvTemperature, tvPrecipitationProbability, tvNavTemp;
    private Button btNavGPS, btNavArea;
    private ImageView ivWeatherIcon, ivNav;
    private RecyclerView rvHourly, rvDaily;

    private SharedPreferences sharedPreferences;
    private boolean tempUnitC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu);
        resources = getResources();
        unknow = resources.getString(R.string.unknow);
        initView();

        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();


        sharedPreferences = getSharedPreferences("WEATHER", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tempUnitC = sharedPreferences.getBoolean("Donvi", true);
    }

    private void initView() {
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCountry = (TextView) findViewById(R.id.tv_country);
        tvTimeZone = (TextView) findViewById(R.id.tv_TimeZone);

        ivWeatherIcon = (ImageView) findViewById(R.id.iv_WeatherIcon);
        ivNav = (ImageView) findViewById(R.id.iv_nav);
        tvWeatherPhrase = (TextView) findViewById(R.id.tv_weatherPhrase);
        tvDayLight = (TextView) findViewById(R.id.tv_dayLight);
        tvTemperature = (TextView) findViewById(R.id.tv_Temperature);
        tvPrecipitationProbability = (TextView) findViewById(R.id.tv_PrecipitationProbability);
        tvNavTemp = (TextView) findViewById(R.id.tv_nav_temp);
        btNavGPS = (Button) findViewById(R.id.bt_nav_gps);
        btNavGPS.setOnClickListener(clickListener);
        btNavArea= (Button) findViewById(R.id.bt_nav_insertArea);
        btNavArea.setOnClickListener(clickListener);

        rvHourly = (RecyclerView) findViewById(R.id.rv_main_hourly);
        rvHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDaily = (RecyclerView) findViewById(R.id.rv_main_daily);
        rvDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_nav_gps:
                    getLocation();
                    break;
                case R.id.bt_nav_insertArea:
                    Intent i=new Intent(MainActivity.this,SearchAreaActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

    private int toCelsius(int fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }


    //Weather theo gsm
    private void getLocation() {

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Map<String, String> mapApiParam = new HashMap<>();
                mapApiParam.put("apikey", API_KEY);
                mapApiParam.put("q", location.getLatitude() + "," + location.getLongitude());

                CustomRequest customRequest = new CustomRequest(Request.Method.GET, API_LOCATION, mapApiParam, responseListenerLocation, errorListener);
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(customRequest, false);
            }
        });
    }

    private Response.Listener<String> responseListenerLocation = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            String localizedName = "LocalizedName";
            JSONObject responseJson = JsonUtil.createJSONObject(response);
//            tvLocation.setText(resources.getString(R.string.your_location) + ": " + JsonUtil.getString(responseJson, localizedName, unknow));
            setTitle(JsonUtil.getString(responseJson, localizedName, unknow));

            JSONObject jsonObject = getJSONObject(responseJson, "AdministrativeArea");
            tvCity.setText(resources.getString(R.string.area_name) + ": " + JsonUtil.getString(jsonObject, localizedName, unknow));

            jsonObject = getJSONObject(responseJson, "Country");
            tvCountry.setText(resources.getString(R.string.country_name) + ": " + JsonUtil.getString(jsonObject, localizedName, unknow));

            jsonObject = getJSONObject(responseJson, "TimeZone");
            tvTimeZone.setText(resources.getString(R.string.timezone) + ": " + JsonUtil.getString(jsonObject, "GmtOffset", unknow));

            getWeather(JsonUtil.getString(responseJson, "Key", unknow));
        }
    };

    //  REQUEST Location Key lấy thời tiết
    private void getWeather(String locationKey) {
        Map<String, String> mapApiParam = new HashMap<>();
        mapApiParam.put("apikey", API_KEY);
        CustomRequest customRequestHourly = new CustomRequest(Request.Method.GET, API_WEATHER_HOURLY + locationKey, mapApiParam, responseListenerWeatherHourly, errorListener);

        mapApiParam = new HashMap<>();
        mapApiParam.put("apikey", API_KEY);
        CustomRequest customRequestDaily = new CustomRequest(Request.Method.GET, API_WEATHER_DAILY + locationKey, mapApiParam, responseListenerWeatherDaily, errorListener);

        MySingleton.getInstance(this).addToRequestQueue(customRequestHourly, false);
        MySingleton.getInstance(this).addToRequestQueue(customRequestDaily, false);
    }


    private Response.Listener<String> responseListenerWeatherHourly = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            JSONArray responseArr = JsonUtil.createJSONArray(response);
            JSONObject jsonObjectNow = JsonUtil.getJSONObject(responseArr, 0);

            int weatherIcon = getInt(jsonObjectNow, "WeatherIcon", 0);
            if (weatherIcon != 0) {
                Glide.with(MainActivity.this).load(URL_WEATHER_ICON + String.format("%02d", weatherIcon) + URL_WEATHER_ICON_TYPE).into(ivWeatherIcon);
                Glide.with(MainActivity.this).load(URL_WEATHER_ICON + String.format("%02d", weatherIcon) + URL_WEATHER_ICON_TYPE).into(ivNav);
            }

            tvWeatherPhrase.setText(resources.getString(R.string.iconPhrase) + ": " + JsonUtil.getString(jsonObjectNow, "IconPhrase", unknow));
            if (JsonUtil.getBoolean(jsonObjectNow, "IsDaylight", false))
                tvDayLight.setText(resources.getString(R.string.day));
            else tvDayLight.setText(resources.getString(R.string.night));

            JSONObject jsonObjectTemp = JsonUtil.getJSONObject(jsonObjectNow, "Temperature");
            int value = getInt(jsonObjectTemp, "Value", FAKETEMP);
            String unit = JsonUtil.getString(jsonObjectTemp, "Unit", "C");
            if (tempUnitC && unit.equals("F")) {
                unit = "C";
                value = toCelsius(value);
            }
            tvTemperature.setText(resources.getString(R.string.temperature) + ": " + value + "°" + unit);
            tvNavTemp.setText(resources.getString(R.string.temperature) + ": " + value + "°" + unit);

            tvPrecipitationProbability.setText(resources.getString(R.string.precipitationProbability) + ": " + JsonUtil.getString(jsonObjectNow, "PrecipitationProbability", unknow) + "%");

            setHourlyWeather(responseArr);
        }
    };

    private Response.Listener<String> responseListenerWeatherDaily = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //setDailyWeather
            RLog.i(response);

            JSONObject jsonObject = JsonUtil.createJSONObject(response);
            JSONArray jsonArray = JsonUtil.getJSONArray(jsonObject, "DailyForecasts");

            int jsonArrayLenght = jsonArray.length();
            ArrayList<WeatherDaily> allWeatherDailies = new ArrayList<>();
            for (int i = 0; i < jsonArrayLenght; i++) {
                JSONObject jsonObjectADay = JsonUtil.getJSONObject(jsonArray, i);
                WeatherDaily weatherDaily = new WeatherDaily();
                weatherDaily.setDateTime(JsonUtil.getString(jsonObjectADay, "Date", unknow));
                JSONObject jsonObjectDay = JsonUtil.getJSONObject(jsonObjectADay, "Day");
                int weatherIcon = getInt(jsonObjectDay, "Icon", 0);
                weatherDaily.setIcon(URL_WEATHER_ICON + String.format("%02d", weatherIcon) + URL_WEATHER_ICON_TYPE);
                JSONObject jsonObjectTemp = JsonUtil.getJSONObject(jsonObjectADay, "Temperature");
                JSONObject jsonObjectMin = JsonUtil.getJSONObject(jsonObjectTemp, "Minimum");
                JSONObject jsonObjectMax = JsonUtil.getJSONObject(jsonObjectTemp, "Maximum");

                int valueMin = JsonUtil.getInt(jsonObjectMin, "Value", FAKETEMP);
                int valueMax = JsonUtil.getInt(jsonObjectMax, "Value", FAKETEMP);
                String unit = JsonUtil.getString(jsonObjectMax, "Unit", "C");
                if (tempUnitC && unit.equals("F")) {
                    unit = "C";
                    valueMin = toCelsius(valueMin);
                    valueMax = toCelsius(valueMax);
                }
                weatherDaily.setTempValueMin(valueMin);
                weatherDaily.setTempValueMAX(valueMax);
                weatherDaily.setTempUnit(unit);

                allWeatherDailies.add(weatherDaily);
            }
            rvDaily.setAdapter(new DailyWeatherAdapter(MainActivity.this, allWeatherDailies));
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            RLog.e(error);
        }
    };

    private void setHourlyWeather(JSONArray responseArr) {
        ArrayList<WeatherHourly> allWeatherHourlies = new ArrayList<>();
        int responseArrLength = responseArr.length();
        for (int i = 0; i < responseArrLength; i++) {
            JSONObject jsonObject = JsonUtil.getJSONObject(responseArr, i);
            WeatherHourly weatherHourly = new WeatherHourly();
            weatherHourly.setDateTime(JsonUtil.getString(jsonObject, "DateTime", unknow));
            int weatherIcon = getInt(jsonObject, "WeatherIcon", 0);
            weatherHourly.setWeatherIcon(URL_WEATHER_ICON + String.format("%02d", weatherIcon) + URL_WEATHER_ICON_TYPE);
            JSONObject jsonObjectTemp = JsonUtil.getJSONObject(jsonObject, "Temperature");
            int value = JsonUtil.getInt(jsonObjectTemp, "Value", FAKETEMP);
            String unit = JsonUtil.getString(jsonObjectTemp, "Unit", "C");
            if (tempUnitC && unit.equals("F")) {
                unit = "C";
                value = toCelsius(value);
            }

            weatherHourly.setTemperatureValue(value);
            weatherHourly.setTemperatureUnit(unit);

            allWeatherHourlies.add(weatherHourly);
        }
        rvHourly.setAdapter(new HourlyWeatherAdapter(this, allWeatherHourlies));
    }
}
