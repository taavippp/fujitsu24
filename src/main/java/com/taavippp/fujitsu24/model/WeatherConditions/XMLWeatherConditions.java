package com.taavippp.fujitsu24.model.WeatherConditions;

import com.taavippp.fujitsu24.model.WeatherPhenomenon;
import com.taavippp.fujitsu24.model.WeatherStation;
import lombok.Getter;
import org.jdom2.Element;
import java.util.Arrays;

@Getter
public class XMLWeatherConditions extends BaseWeatherConditions {
    private final WeatherStation weatherStation;

    public XMLWeatherConditions(Element element) {
        String name = element.getChildTextTrim("name");
        String sWmoCode = element.getChildTextTrim("wmocode");
        if (sWmoCode.isEmpty()) {
            this.weatherStation = new WeatherStation(name, -1);
            return;
        }
        int wmoCode = Integer.parseInt(sWmoCode);
        this.weatherStation = new WeatherStation(name, wmoCode);
        try {
            this.airTemperature = Float.parseFloat(element.getChildTextTrim("airtemperature"));
            this.windSpeed = Float.parseFloat(element.getChildTextTrim("windspeed"));
            String swp = element.getChildTextTrim("phenomenon");
            Arrays.stream(WeatherPhenomenon.values())
                    .forEach(wp -> {
                        if (wp.name.equals(swp)) {
                            this.weatherPhenomenon = wp;
                        }
                    });
        } catch (NumberFormatException ignored) {}
    }
}
