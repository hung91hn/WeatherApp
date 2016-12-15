package com.android1.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android1.weather.adapter.DailyWeatherAdapter;
import com.android1.weather.adapter.HourlyWeatherAdapter;
import com.android1.weather.model.WeatherDaily;
import com.android1.weather.model.WeatherHourly;
import com.android1.weather.model.WeatherLocation;
import com.android1.weather.services.AutoUpdateService;
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

import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static com.android1.weather.utils.JsonUtil.getInt;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = "JVZgNJ8lEM4EpcoHyuQvEePb3HjPS6A4";
    private final String API_LOCATION = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
    public static final String API_WEATHER_HOURLY = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/";
    private final String API_WEATHER_DAILY = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
    private final String URL_WEATHER_ICON = "http://developer.accuweather.com/sites/default/files/";
    private final String URL_WEATHER_ICON_TYPE = "-s.png";
    public static final String KEY_WEATHER = "WEATHER";
    public static final String KEY_LOCATION = "LocaKey";
    private final int FAKETEMP = 10000;
    private final int MAX_LOCATION_NUMBER = 5;
    private String unknow;
    private Resources resources;
    private GoogleApiClient mClient;
    private TextView tvCity, tvCountry, tvTimeZone, tvWeatherPhrase, tvDayLight, tvTemperature, tvPrecipitationProbability, tvNavTemp, tvInsertArea;
    private Button btNavArea;
    private ImageButton btNavGPS;
    private ImageView ivWeatherIcon, ivNav;
    private RecyclerView rvHourly, rvDaily;
    private ListView lvNavArea;
    private SwipeRefreshLayout srlMain;

    private SharedPreferences sharedPreferences;
    private boolean tempUnitC;
    public static boolean changeSetting = false;

    private Realm realm;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent pushIntent = new Intent(this, AutoUpdateService.class);
        startService(pushIntent);

        resources = getResources();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable drawable = resources.getDrawable(R.drawable.menu);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        actionBar.setHomeAsUpIndicator(drawable);
        unknow = resources.getString(R.string.unknow);
        initView();

        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();


        sharedPreferences = getSharedPreferences(KEY_WEATHER, MODE_PRIVATE);

        realm = Realm.getDefaultInstance();

        tempUnitC = sharedPreferences.getBoolean(SettingActivity.TYPE_TEMP, true);

        setListLocation();
        reloadCurrentWeather();
    }

    private void reloadCurrentWeather() {
        String locaKey = sharedPreferences.getString(KEY_LOCATION, null);
        if (locaKey != null) {
            RealmResults<WeatherLocation> allLocations = realm.where(WeatherLocation.class).findAll();
            for (WeatherLocation location : allLocations) {
                if (location.getLocationKey().equals(locaKey))
                    setLocationAndWeather(location);
            }
        }
    }

    private void initView() {
        srlMain = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        srlMain.setOnRefreshListener(refreshListener);

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
        tvInsertArea = (TextView) findViewById(R.id.tv_insertArea);
        btNavGPS = (ImageButton) findViewById(R.id.bt_nav_gps);
        btNavGPS.setOnClickListener(clickListener);
        btNavArea = (Button) findViewById(R.id.bt_nav_insertArea);
        btNavArea.setOnClickListener(clickListener);

        rvHourly = (RecyclerView) findViewById(R.id.rv_main_hourly);
        rvHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDaily = (RecyclerView) findViewById(R.id.rv_main_daily);
        rvDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lvNavArea = (ListView) findViewById(R.id.lv_area);
        lvNavArea.setOnItemClickListener(itemClickListener);
        lvNavArea.setOnItemLongClickListener(itemLongClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (changeSetting) {
            changeSetting = false;
            tempUnitC = sharedPreferences.getBoolean(SettingActivity.TYPE_TEMP, true);
            reloadCurrentWeather();
        }
    }

    private void setListLocation() {
        RealmResults<WeatherLocation> allLocations = realm.where(WeatherLocation.class).findAll();
        int size = allLocations.size();

        ArrayList<String> allLocaName = new ArrayList<>();
        for (int i = 0; i < size; i++)
            allLocaName.add(allLocations.get(i).toString());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allLocaName);
        lvNavArea.setAdapter(adapter);

        if (allLocations.size() < MAX_LOCATION_NUMBER) {
            tvInsertArea.setText(R.string.insert_area);
            btNavArea.setVisibility(View.VISIBLE);
            btNavGPS.setVisibility(View.VISIBLE);
        } else {
            tvInsertArea.setText(R.string.delete_location);
            btNavArea.setVisibility(GONE);
            btNavGPS.setVisibility(GONE);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            WeatherLocation location = data.getExtras().getParcelable("loca");
            addLocationToRealm(location);
        }
    }

    private void addLocationToRealm(WeatherLocation location) {
        if (realm.where(WeatherLocation.class).findAll().size() < MAX_LOCATION_NUMBER) {
            realm.beginTransaction();
            WeatherLocation weatherLocation = realm.copyToRealmOrUpdate(location);
            realm.insertOrUpdate(weatherLocation);
            realm.commitTransaction();
            setListLocation();
        }
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
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            reloadCurrentWeather();
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_nav_gps:
                    getLocation();
                    break;
                case R.id.bt_nav_insertArea:
                    Intent i = new Intent(MainActivity.this, SearchAreaActivity.class);
                    startActivityForResult(i, 100);
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            RealmResults<WeatherLocation> allLocations = realm.where(WeatherLocation.class).findAll();
            WeatherLocation location = allLocations.get(i);
            setLocationAndWeather(location);

        }
    };

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            realm.beginTransaction();
            RealmResults<WeatherLocation> allLocations = realm.where(WeatherLocation.class).findAll();
            allLocations.deleteFromRealm(i);
            realm.commitTransaction();

            setListLocation();
            return true;
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
            JSONObject responseJson = JsonUtil.createJSONObject(response);
            WeatherLocation weatherLocation = SearchAreaActivity.getLocationInfo(responseJson);
            addLocationToRealm(weatherLocation);
            setListLocation();
            setLocationAndWeather(weatherLocation);
        }
    };

    private void setLocationAndWeather(WeatherLocation weatherLocation) {
        sharedPreferences.edit().putString(KEY_LOCATION, weatherLocation.getLocationKey()).apply();
        setTitle(weatherLocation.getLocalizedName());
        tvCity.setText(resources.getString(R.string.area_name) + ": " + weatherLocation.getArea());
        tvCountry.setText(resources.getString(R.string.country_name) + ": " + weatherLocation.getCountry());
        tvTimeZone.setText(resources.getString(R.string.timezone) + ": " + weatherLocation.getTimeZone());

        setWeather(weatherLocation.getLocationKey());
    }

    //  REQUEST Location Key lấy thời tiết
    private void setWeather(String locationKey) {
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
                final String iconInx = String.format("%02d", weatherIcon);
                String imageUrl = URL_WEATHER_ICON + iconInx + URL_WEATHER_ICON_TYPE;
                Glide.with(MainActivity.this).load(imageUrl).into(ivWeatherIcon);
                Glide.with(MainActivity.this).load(imageUrl).into(ivNav);
            }

            tvWeatherPhrase.setText(resources.getString(R.string.iconPhrase) + ": " + JsonUtil.getString(jsonObjectNow, "IconPhrase", unknow));
            if (JsonUtil.getBoolean(jsonObjectNow, "IsDaylight", false))
                tvDayLight.setText(R.string.day);
            else tvDayLight.setText(R.string.night);

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
            srlMain.setRefreshing(false);
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
