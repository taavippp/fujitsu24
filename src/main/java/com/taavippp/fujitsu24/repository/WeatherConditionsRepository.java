package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import org.springframework.data.jpa.repository.JpaRepository;

/*
* All the regions' weather conditions are stored in the database table that this interface creates.
* The table will be called "WEATHER_CONDITIONS"
* */
public interface WeatherConditionsRepository extends JpaRepository<WeatherConditions, Long> {}