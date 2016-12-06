package com.android1.weather.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android1.weather.R;
import com.android1.weather.model.WeatherDaily;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hung91hn on 12/1/16.
 */

public class DailyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherHolder> {
    private Activity activity;
    private ArrayList<WeatherDaily> allWeatherDailies;
    private SimpleDateFormat sdf;

    public DailyWeatherAdapter(Activity activity, ArrayList<WeatherDaily> allWeatherHourlies) {
        this.activity = activity;
        this.allWeatherDailies = allWeatherHourlies;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    @Override
    public HourlyWeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.adapter_weather_hourly, parent, false);
        return new HourlyWeatherHolder(itemview);
    }

    @Override
    public void onBindViewHolder(HourlyWeatherHolder holder, int position) {
        WeatherDaily weatherDaily = allWeatherDailies.get(position);
        try {
            Date dateTime = sdf.parse(weatherDaily.getDateTime());
            String thisDay= activity.getResources().getStringArray(R.array.day_in_week)[dateTime.getDay()];
            holder.tvTime.setText(thisDay);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(activity).load(weatherDaily.getIcon()).into(holder.ivIcon);
        holder.tvTemp.setText(weatherDaily.getTempValueMin()
                + "-" + weatherDaily.getTempValueMAX() + "°" + weatherDaily.getTempUnit());
    }

    @Override
    public int getItemCount() {
        return allWeatherDailies.size();
    }
}
