package com.taavippp.fujitsu24.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor
public class WeatherConditions extends BaseWeatherConditions {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String station;
    private int wmoCode;
    private long timestamp;

    public WeatherConditions(WeatherStation weatherStation, float airTemperature, float windSpeed, WeatherPhenomenon weatherPhenomenon, long timestamp) {
        this.station = weatherStation.getName();
        this.wmoCode = weatherStation.getWmoCode();
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.timestamp = timestamp;
    }
}
