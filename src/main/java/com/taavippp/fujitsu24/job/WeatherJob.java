package com.taavippp.fujitsu24.job;

import com.taavippp.fujitsu24.model.XMLWeatherConditions;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import com.taavippp.fujitsu24.service.WeatherService;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

@Component
public class WeatherJob {
    private final WeatherService weatherService = new WeatherService();
    private static final Logger logger = LoggerFactory.getLogger(WeatherJob.class);
    @Autowired WeatherConditionsRepository weatherConditionsRepository;

    //@Scheduled(cron = "15 * * * *") // Production
    @Scheduled(cron = "15 * * * * *") // Development
    private void updateWeatherConditions() throws URISyntaxException, IOException, JDOMException {
        logger.info("Weather job started");
        String data = weatherService.requestRawWeatherData();
        Stream<XMLWeatherConditions> xmlData = weatherService.deserializeXMLWeatherData(data);
        weatherService.saveWeatherData(xmlData, weatherConditionsRepository);
    }
}