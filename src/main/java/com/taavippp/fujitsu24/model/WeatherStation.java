package com.taavippp.fujitsu24.model;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class WeatherStation {
    private String name;
    private int wmoCode;
}
