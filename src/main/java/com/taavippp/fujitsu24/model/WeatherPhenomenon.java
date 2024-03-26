package com.taavippp.fujitsu24.model;

/*
* This enum represents ALL of the different weather phenomenons that the https://ilmateenistus.ee API
* can return. The variable "name" exists to match the phenomenon in the way the API request would return it.
* */
public enum WeatherPhenomenon {
    CLEAR("Clear"),
    FEW_CLOUDS("Few clouds"),
    VARIABLE_CLOUDS("Variable clouds"),
    CLOUDY_WITH_CLEAR_SPELLS("Cloudy with clear spells"),
    OVERCAST("Overcast"),
    LIGHT_SNOW_SHOWER("Light snow shower"),
    MODERATE_SNOW_SHOWER("Moderate snow shower"),
    HEAVY_SNOW_SHOWER("Heavy snow shower"),
    LIGHT_SHOWER("Light shower"),
    MODERATE_SHOWER("Moderate shower"),
    HEAVY_SHOWER("Heavy shower"),
    LIGHT_RAIN("Light rain"),
    MODERATE_RAIN("Moderate rain"),
    HEAVY_RAIN("Heavy rain"),
    GLAZE("Glaze"),
    LIGHT_SLEET("Light sleet"),
    MODERATE_SLEET("Moderate sleet"),
    LIGHT_SNOWFALL("Light snowfall"),
    MODERATE_SNOWFALL("Moderate snowfall"),
    HEAVY_SNOWFALL("Heavy snowfall"),
    BLOWING_SNOW("Blowing snow"),
    DRIFTING_SNOW("Drifting snow"),
    HAIL("Hail"),
    MIST("Mist"),
    FOG("Fog"),
    THUNDER("Thunder"),
    THUNDERSTORM("Thunderstorm");

    public final String name;

    WeatherPhenomenon(String name) {
        this.name = name;
    }
}
