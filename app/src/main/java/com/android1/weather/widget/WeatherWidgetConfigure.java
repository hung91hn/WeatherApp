package com.android1.weather.widget;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.android1.weather.R;
import com.android1.weather.services.WidgetUpdateService;

/**
 * Created by CYCE on 08/12/2016.
 */

public class WeatherWidgetConfigure extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.widget_layout);
        new WidgetUpdateService();
    }
}
