package com.android1.weather.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.android1.weather.R;
import com.android1.weather.widget.WidgetHourlyProvider;

/**
 * Created by CYCE on 09/12/2016.
 */

public class WidgetUpdateService extends Service {
    private static final String TAG="WidgetService";

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG,"called");

        AppWidgetManager appWidgetManager= AppWidgetManager.getInstance(this.getApplicationContext());
        int[] allWidgetIds=intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        ComponentName thisWidget=new ComponentName(getApplicationContext(), WidgetHourlyProvider.class);
        int[] allWidgetIds2=appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds)
        {
            RemoteViews remoteViews=new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.txtLocal,String.valueOf(R.string.area_name));
            remoteViews.setImageViewResource(R.id.imgPre,R.id.iv_WeatherIcon);
            remoteViews.setTextViewText(R.id.txtTemp,String.valueOf(R.string.temperature));
        }
        stopSelf();
        super.onStart(intent, startId);
    }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
