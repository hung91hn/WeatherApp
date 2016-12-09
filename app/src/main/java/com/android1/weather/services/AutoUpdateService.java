package com.android1.weather.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android1.weather.MainActivity;
import com.android1.weather.R;
import com.android1.weather.SettingActivity;
import com.android1.weather.model.WeatherLocation;
import com.android1.weather.utils.CustomRequest;
import com.android1.weather.utils.JsonUtil;
import com.android1.weather.utils.MySingleton;
import com.android1.weather.utils.RLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.android1.weather.utils.JsonUtil.getInt;

public class AutoUpdateService extends Service {
    private int timeUpdate;
    private SharedPreferences preferences;
//    private Realm realm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Khởi tạo ở đấy
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(MainActivity.KEY_WEATHER, MODE_PRIVATE);
//        realm = Realm.getDefaultInstance();
        timeUpdate = preferences.getInt(SettingActivity.TIME_UPDATE, 1);
    }

    // làm các thú ở đây
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                String locaKey = preferences.getString(MainActivity.KEY_LOCATION, null);
                if (locaKey != null) {
                    RealmResults<WeatherLocation> allLocations = Realm.getDefaultInstance().where(WeatherLocation.class).findAll();
                    for (WeatherLocation location : allLocations) {
                        if (location.getLocationKey().equals(locaKey)) {
                            setLocationAndWeather(location);
                            break;
                        }
                    }
                }
            }
        };
        timer.schedule(hourlyTask, 0l, 1000 * 60 * 60 * timeUpdate);
        return START_STICKY;
    }

    private void setLocationAndWeather(WeatherLocation weatherLocation) {
        Map<String, String> mapApiParam = new HashMap<>();
        mapApiParam.put("apikey", MainActivity.API_KEY);
        CustomRequest customRequestHourly = new CustomRequest(Request.Method.GET, MainActivity.API_WEATHER_HOURLY + weatherLocation.getLocationKey(), mapApiParam, responseListenerWeatherHourly, errorListener);
        MySingleton.getInstance(this).addToRequestQueue(customRequestHourly, false);
    }

    private Response.Listener<String> responseListenerWeatherHourly = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            JSONArray responseArr = JsonUtil.createJSONArray(response);
            JSONObject jsonObjectNow = JsonUtil.getJSONObject(responseArr, 2);

            int weatherIcon = getInt(jsonObjectNow, "WeatherIcon", 0);

            int[] badWeathers = {12, 13, 14, 15, 16, 17, 18, 22, 24, 25, 26, 29, 39, 40, 41, 42, 43, 44};
            for (int i : badWeathers) {
                if (weatherIcon == i) {
                    // hieenj notifi
                    Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(AutoUpdateService.this, 0, intent, 0);
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.weather_15);
                    Notification notification = new NotificationCompat.Builder(AutoUpdateService.this)
                            .setSmallIcon(R.mipmap.weather_15)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText("Thời tiết xấu")
                            .setContentIntent(pi)
                            .build();
                    NotificationManagerCompat.from(AutoUpdateService.this).notify(0, notification);
                    break;
                }
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            RLog.e(error);
        }
    };


    //Không hủy nên không onDestroy
}
