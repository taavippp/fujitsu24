package com.taavippp.fujitsu24.model;

import lombok.Getter;
import org.jdom2.Element;
import java.util.Arrays;

@Getter
public class XMLWeatherConditions extends BaseWeatherConditions {
    WeatherStation weatherStation;

    public XMLWeatherConditions(Element element) {
        String name = element.getChildTextTrim("name");
        try {
            int wmoCode = Integer.parseInt(element.getChildTextTrim("wmocode"));
            this.weatherStation = new WeatherStation(name, wmoCode);
            this.airTemperature = Float.parseFloat(element.getChildTextTrim("airtemperature"));
            this.windSpeed = Float.parseFloat(element.getChildTextTrim("windspeed"));
            String swp = element.getChildTextTrim("phenomenon");
            Arrays.stream(WeatherPhenomenon.values())
                    .forEach(wp -> {
                        if (wp.name.equals(swp)) {
                            this.weatherPhenomenon = wp;
                        }
                    });
        } catch (NumberFormatException err) {
            this.weatherStation = new WeatherStation(name, -1);
        }
    }
}
