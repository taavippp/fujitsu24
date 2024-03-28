package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/*
* All the regions' weather conditions are stored in the database table that this interface creates.
* The table will be called "WEATHER_CONDITIONS"
* */
public interface WeatherConditionsRepository extends JpaRepository<WeatherConditions, Long> {
    @Query(value =
            "SELECT * " +
            "FROM WEATHER_CONDITIONS WC " +
            "WHERE WC.STATION = :station " +
            "AND WC.WMO_CODE = :wmo " +
            "AND TIMESTAMP = :timestamp",
            nativeQuery = true
    )
    public Optional<WeatherConditions> findOneByWeatherStationAndTimestamp(
            @Param("station") String station, @Param("wmo") int wmoCode, @Param("timestamp") long timestamp
    );
}