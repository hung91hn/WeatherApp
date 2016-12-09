package com.android1.weather.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android1.weather.services.WidgetUpdateService;

/**
 * Created by CYCE on 08/12/2016.
 */

public class WidgetHourlyProvider extends AppWidgetProvider {
    private final String WIDGET_TAG="WidgetProvider";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(WIDGET_TAG,"onUpdate called");
        ComponentName WeatherWidget=new ComponentName(context,WidgetHourlyProvider.class);
        int[] allWidgetIds=appWidgetManager.getAppWidgetIds(WeatherWidget);

        Intent intent= new Intent(context.getApplicationContext(), WidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);


        context.startService(intent);
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
