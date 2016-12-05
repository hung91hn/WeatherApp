package com.android1.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android1.weather.R;

/**
 * Created by hung91hn on 12/1/16.
 */

public class HourlyWeatherHolder extends RecyclerView.ViewHolder{
    TextView tvTime, tvTemp;
    ImageView ivIcon;
    public HourlyWeatherHolder(View itemView) {
        super(itemView);
        tvTime= (TextView) itemView.findViewById(R.id.tv_hourly_time);
        ivIcon= (ImageView) itemView.findViewById(R.id.iv_hourly_icon);
        tvTemp= (TextView) itemView.findViewById(R.id.tv_hourly_temp);
    }
}
