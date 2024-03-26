package com.taavippp.fujitsu24.model;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/*
* This helper class represents a weather station.
* A weather station has a WMO code and a name.
* This class is used in the enum Region.
* */
@Getter @Setter @AllArgsConstructor
public class WeatherStation {
    private String name;
    private int wmoCode;
}
