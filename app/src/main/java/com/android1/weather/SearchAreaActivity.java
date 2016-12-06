package com.android1.weather;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android1.weather.model.WeatherLocation;
import com.android1.weather.utils.CustomRequest;
import com.android1.weather.utils.JsonUtil;
import com.android1.weather.utils.MySingleton;
import com.android1.weather.utils.RLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchAreaActivity extends AppCompatActivity {
    private final String API_KEY = "JVZgNJ8lEM4EpcoHyuQvEePb3HjPS6A4";
    private final String API_SEARCH_LOCATION = "http://dataservice.accuweather.com/locations/v1/search";

    private TextView tvSearchResult;
    private ListView lvSearchResult;

    private String nullLocation, selectLocation;

    private ArrayList<WeatherLocation> listLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_area);
        setTitle(getResources().getString(R.string.insert_area));

        tvSearchResult = (TextView) findViewById(R.id.tv_searchArea);
        lvSearchResult = (ListView) findViewById(R.id.lv_searchArea);

        Resources resources = getResources();

        nullLocation = resources.getString(R.string.null_location);
        selectLocation = resources.getString(R.string.select_location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_area, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_area_sv).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Map<String, String> mapApiParam = new HashMap<>();
            mapApiParam.put("apikey", API_KEY);
            mapApiParam.put("q", s);
            CustomRequest customRequest = new CustomRequest(Request.Method.GET, API_SEARCH_LOCATION, mapApiParam, responseSearchLocation, errorListener);
            MySingleton.getInstance(SearchAreaActivity.this).addToRequestQueue(customRequest, false);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };


    private Response.Listener<String> responseSearchLocation = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            listLocations = new ArrayList<>();
            ArrayList<String> listLoca = new ArrayList<>();

            JSONArray jsonArray = JsonUtil.createJSONArray(response);
            int lenght = jsonArray.length();
            Log.i("ListView", "size OJ " + lenght);
            if (lenght != 0) {
                tvSearchResult.setText(nullLocation);
                for (int i = 0; i < lenght; i++) {
                    WeatherLocation weatherLocation = new WeatherLocation();
                    JSONObject jsonObject = JsonUtil.getJSONObject(jsonArray, i);
                    weatherLocation.setLocationKey(JsonUtil.getString(jsonObject, "Key", null));
                    String locaName = JsonUtil.getString(jsonObject, "LocalizedName", null);
                    weatherLocation.setLocalizedName(locaName);
                    listLoca.add(locaName);
                    JSONObject jsonObject2 = JsonUtil.getJSONObject(jsonObject, "AdministrativeArea");
                    weatherLocation.setArea(JsonUtil.getString(jsonObject2, "LocalizedName", null));
                    jsonObject2 = JsonUtil.getJSONObject(jsonObject, "Country");
                    weatherLocation.setCountry(JsonUtil.getString(jsonObject2, "LocalizedName", null));
                    jsonObject2 = JsonUtil.getJSONObject(jsonObject, "Region");
                    weatherLocation.setRegion(JsonUtil.getString(jsonObject2, "LocalizedName", null));
                    jsonObject2 = JsonUtil.getJSONObject(jsonObject, "TimeZone");
                    weatherLocation.setTimeZone(JsonUtil.getInt(jsonObject2, "GmtOffset", 0));
                    listLocations.add(weatherLocation);
                }
                Log.i("ListView", "size list " + listLoca.size());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchAreaActivity.this, android.R.layout.simple_list_item_1, listLoca);
                lvSearchResult.setAdapter(adapter);
            } else {
                tvSearchResult.setText(selectLocation);
                lvSearchResult.setAdapter(null);
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            RLog.e(error);
        }
    };

}
