package com.android1.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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

    private String finddingLocation, nullLocation, selectLocation;

    private ArrayList<WeatherLocation> listLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_area);
        setTitle(getResources().getString(R.string.insert_area));

        tvSearchResult = (TextView) findViewById(R.id.tv_searchArea);
        lvSearchResult = (ListView) findViewById(R.id.lv_searchArea);
        lvSearchResult.setOnItemClickListener(itemClickListener);

        Resources resources = getResources();

        finddingLocation = resources.getString(R.string.findding);
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
            tvSearchResult.setText(finddingLocation);
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

    public static WeatherLocation getLocationInfo(JSONObject jsonObject) {
        WeatherLocation weatherLocation = new WeatherLocation();
        weatherLocation.setLocationKey(JsonUtil.getString(jsonObject, "Key", null));
        String locaName = JsonUtil.getString(jsonObject, "LocalizedName", null);
        weatherLocation.setLocalizedName(locaName);
        JSONObject jsonObject2 = JsonUtil.getJSONObject(jsonObject, "AdministrativeArea");
        weatherLocation.setArea(JsonUtil.getString(jsonObject2, "LocalizedName", null));
        jsonObject2 = JsonUtil.getJSONObject(jsonObject, "Country");
        weatherLocation.setCountry(JsonUtil.getString(jsonObject2, "LocalizedName", null));
        jsonObject2 = JsonUtil.getJSONObject(jsonObject, "Region");
        weatherLocation.setRegion(JsonUtil.getString(jsonObject2, "LocalizedName", null));
        jsonObject2 = JsonUtil.getJSONObject(jsonObject, "TimeZone");
        weatherLocation.setTimeZone(JsonUtil.getInt(jsonObject2, "GmtOffset", 0));
        return weatherLocation;
    }

    private Response.Listener<String> responseSearchLocation = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            listLocations = new ArrayList<>();

            JSONArray jsonArray = JsonUtil.createJSONArray(response);
            int lenght = jsonArray.length();
            if (lenght != 0) {
                ArrayList<String> loca = new ArrayList<>();
                tvSearchResult.setText(selectLocation);
                for (int i = 0; i < lenght; i++) {
                    WeatherLocation weatherLocation = getLocationInfo(JsonUtil.getJSONObject(jsonArray, i));

                    listLocations.add(weatherLocation);
                    loca.add(weatherLocation.toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchAreaActivity.this, android.R.layout.simple_list_item_1, loca);
                lvSearchResult.setAdapter(adapter);
            } else {
                tvSearchResult.setText(nullLocation);
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

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent returnIntent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("loca", listLocations.get(i));
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    };

}
