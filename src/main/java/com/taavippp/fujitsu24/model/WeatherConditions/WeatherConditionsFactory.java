package com.taavippp.fujitsu24.model.WeatherConditions;

import com.taavippp.fujitsu24.model.WeatherPhenomenon;
import org.jdom2.Element;

import java.util.Arrays;
import java.util.Optional;

/*
* For comfortably creating new instances of WeatherConditions.
* Currently only contains a method for creating one from an XML element.
* */
public class WeatherConditionsFactory {
    /*
    * Creates WeatherConditions instance from an XML element.
    * Only the name is mandatory for WeatherConditions, for everything else,
    * a default value (that may be incorrect in actuality) exists.
    * */
    public static WeatherConditions createFromXML(Element element) {
        int wmoCode = -1;
        float airTemperature = 0;
        float windSpeed = 0;
        WeatherPhenomenon weatherPhenomenon = WeatherPhenomenon.CLEAR;

        String name = element.getChildTextTrim("name");
        String sWmoCode = element.getChildTextTrim("wmocode");
        if (!sWmoCode.isBlank()) {
            wmoCode = Integer.parseInt(sWmoCode);
        }
        String sAirTemperature = element.getChildTextTrim("airtemperature");
        if (!sAirTemperature.isBlank()) {
            airTemperature = Float.parseFloat(sAirTemperature);
        }
        String sWindSpeed = element.getChildTextTrim("windspeed");
        if (!sWindSpeed.isBlank()) {
            windSpeed = Float.parseFloat(sWindSpeed);
        }
        String sWeatherPhenomenon = element.getChildTextTrim("phenomenon");
        Optional<WeatherPhenomenon> optionalWeatherPhenomenon = Arrays.stream(WeatherPhenomenon.values())
                .filter(wp -> wp.name.equals(sWeatherPhenomenon)).findFirst();
        if (optionalWeatherPhenomenon.isPresent()) {
            weatherPhenomenon = optionalWeatherPhenomenon.get();
        }

        return new WeatherConditions(
                name,
                wmoCode,
                airTemperature,
                windSpeed,
                weatherPhenomenon
        );
    }
}
