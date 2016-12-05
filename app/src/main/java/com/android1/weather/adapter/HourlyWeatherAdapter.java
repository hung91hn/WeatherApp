package com.android1.weather.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android1.weather.R;
import com.android1.weather.model.WeatherHourly;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hung91hn on 12/1/16.
 */

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherHolder> {
    private Activity activity;
    private ArrayList<WeatherHourly> allWeatherHourlies;
    private SimpleDateFormat sdf;

    public HourlyWeatherAdapter(Activity activity, ArrayList<WeatherHourly> allWeatherHourlies) {
        this.activity = activity;
        this.allWeatherHourlies = allWeatherHourlies;
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
        WeatherHourly weatherHourly = allWeatherHourlies.get(position);
        try {
            Date dateTime = sdf.parse(weatherHourly.getDateTime());
            holder.tvTime.setText(dateTime.getHours()+"h");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(activity).load(weatherHourly.getWeatherIcon()).into(holder.ivIcon);
        holder.tvTemp.setText(weatherHourly.getTemperatureValue() + "°" + weatherHourly.getTemperatureUnit());
    }

    @Override
    public int getItemCount() {
        return allWeatherHourlies.size();
    }
}
