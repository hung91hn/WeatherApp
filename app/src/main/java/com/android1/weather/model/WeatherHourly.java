package com.android1.weather.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hung91hn on 12/1/16.
 */

public class WeatherHourly extends RealmObject {
    @PrimaryKey
    private String dateTime;
    private String weatherIcon, iconPhrase, temperatureUnit, mobileLink;
    private int temperatureValue, precipitationProbability;
    private boolean daylight;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIconPhrase() {
        return iconPhrase;
    }

    public void setIconPhrase(String iconPhrase) {
        this.iconPhrase = iconPhrase;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getMobileLink() {
        return mobileLink;
    }

    public void setMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public int getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(int temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public int getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(int precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public boolean isDaylight() {
        return daylight;
    }

    public void setDaylight(boolean daylight) {
        this.daylight = daylight;
    }
}
