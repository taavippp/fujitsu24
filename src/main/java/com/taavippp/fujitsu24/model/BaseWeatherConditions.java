package com.taavippp.fujitsu24.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public class BaseWeatherConditions {
    float airTemperature;
    float windSpeed;
    WeatherPhenomenon weatherPhenomenon;
}
