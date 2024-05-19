package com.cs310.weatherapp;

public class WeatherRV {
    private String time;
    private String temp;
    private String icon;
    private String windSpeed;
    private boolean isC;

    public WeatherRV(String time, String temp, String icon, String windSpeed, boolean isC) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.windSpeed = windSpeed;
        this.isC = isC;
    }

    public String getTime() {
        return time;
    }

    public String getTemp() {
        return temp;
    }

    public String getIcon() {
        return icon;
    }
    public boolean getIsC() {
        return isC;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setIsC(boolean isC) {
        this.isC = isC;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
