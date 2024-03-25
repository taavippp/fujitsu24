package com.taavippp.fujitsu24.model;

public enum Region {
    TALLINN(new WeatherStation("Tallinn-Harku", 26038)),
    TARTU(new WeatherStation("Tartu-Tõravere", 26242)),
    PARNU(new WeatherStation("Pärnu", 41803));

    public final WeatherStation weatherStation;

    Region(WeatherStation station) {
        this.weatherStation = station;
    }
}
