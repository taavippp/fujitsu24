package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.WeatherConditions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherConditionsRepository extends JpaRepository<WeatherConditions, Long> {}