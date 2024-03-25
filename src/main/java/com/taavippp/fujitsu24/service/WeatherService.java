package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.WeatherConditions;
import com.taavippp.fujitsu24.model.XMLWeatherConditions;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class WeatherService {
    private static final String weatherURL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public String requestRawWeatherData() throws URISyntaxException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(weatherURL))
                .GET()
                .build();
        try {
            HttpResponse<String> res = HttpClient.newBuilder()
                    .build()
                    .send(
                            req,
                            HttpResponse.BodyHandlers.ofString()
                    );
            return res.body();
        } catch (IOException | InterruptedException err) {
            logger.error("An error occurred with the weather data request: " + err);
            return null;
        }
    }

    public Stream<XMLWeatherConditions> deserializeXMLWeatherData(String data) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(
                new StringReader(data)
        );
        Element root = document.getRootElement();
        Stream<XMLWeatherConditions> xmlWeatherConditions = root
                .getChildren()
                .stream()
                .map(XMLWeatherConditions::new);
        Stream<XMLWeatherConditions> validWeatherConditions = xmlWeatherConditions
                .filter(
                        wc -> Arrays.stream(Region.values()).anyMatch(
                                region -> region.weatherStation.getName().equals(wc.getWeatherStation().getName())
                        )
                );
        return validWeatherConditions;
    }

    public void saveWeatherData(Stream<XMLWeatherConditions> data, WeatherConditionsRepository weatherConditionsRepository) {
        long timestamp = ZonedDateTime.now().toEpochSecond();
        data.forEach(
                xwc -> {
                    WeatherConditions wc = new WeatherConditions(
                            xwc.getWeatherStation(),
                            xwc.getAirTemperature(),
                            xwc.getWindSpeed(),
                            xwc.getWeatherPhenomenon(),
                            timestamp
                    );
                    weatherConditionsRepository.save(wc);
                }
        );
        weatherConditionsRepository.flush();
    }
}
