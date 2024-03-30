package com.taavippp.fujitsu24.job;

import com.taavippp.fujitsu24.config.WeatherJobConfig;
import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import com.taavippp.fujitsu24.service.WeatherService;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.stream.Stream;

/*
* This class performs the required CronJob:
* once every hour, on the 15th minute, the newest weather data is fetched from the Internet.
* The weather data gets parsed, deserialized, filtered and is then inserted into the DB.
* */
@Component
public class WeatherJob {
    private @Autowired WeatherService weatherService;
    private static final Logger logger = LoggerFactory.getLogger(WeatherJob.class);

    @Scheduled(cron = WeatherJobConfig.cronExpression)
    private void updateWeatherConditions() throws URISyntaxException, IOException, JDOMException {
        logger.info("Weather job started");
        long timestamp = Instant.now().getEpochSecond();
        String data = weatherService.requestRawWeatherData();
        Stream<WeatherConditions> weatherConditionsStream = weatherService.deserializeXMLWeatherData(data);
        weatherService.saveWeatherData(weatherConditionsStream, timestamp);
        logger.info("Weather job completed");
    }
}
