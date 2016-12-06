package com.android1.weather.model;

/**
 * Created by hung91hn on 12/5/16.
 */

public class WeatherLocation {
    private String locationKey, localizedName, area, Country, Region;
    private int TimeZone;

    public String getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public int getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(int timeZone) {
        TimeZone = timeZone;
    }
}
