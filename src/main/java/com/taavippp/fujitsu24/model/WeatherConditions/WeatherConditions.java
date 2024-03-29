package com.taavippp.fujitsu24.model.WeatherConditions;

import com.taavippp.fujitsu24.model.WeatherPhenomenon;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

/*
* This class represents the weather conditions that are fetched from the Ilmateenistus.ee API and inserted into the DB.
* */
@Entity
@Getter @RequiredArgsConstructor @AllArgsConstructor @NoArgsConstructor
public class WeatherConditions {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private @NonNull String station;
    private @NonNull Integer wmoCode;
    private @NonNull Float airTemperature;
    private @NonNull Float windSpeed;
    private @NonNull WeatherPhenomenon weatherPhenomenon;
    @Setter private long timestamp;
}
