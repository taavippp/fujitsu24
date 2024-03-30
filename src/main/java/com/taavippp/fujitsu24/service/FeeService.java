package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.*;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/*
* This class contains every method related to inserting the constant initial fees from
* their respective .xml files.
* */
@Service("fee-service")
public class FeeService implements IFeeService {
    private static final Logger logger = LoggerFactory.getLogger(FeeService.class);
    private static final String initialRegionalFeesFilename = "InitialRegionalFees.xml";
    private static final String initialExtraFeesFilename = "InitialExtraFees.xml";
    private @Autowired WeatherConditionsRepository weatherConditionsRepository;
    private @Autowired RegionalFeeRepository regionalFeeRepository;
    private @Autowired ExtraFeeRepository extraFeeRepository;
    private final Map<ExtraFeeCategory, List<WeatherPhenomenon>> wpefMap = Map.ofEntries(
            Map.entry(ExtraFeeCategory.WPEF_SNOW_SLEET, List.of(
                    WeatherPhenomenon.BLOWING_SNOW,
                    WeatherPhenomenon.DRIFTING_SNOW,
                    WeatherPhenomenon.LIGHT_SNOWFALL,
                    WeatherPhenomenon.MODERATE_SNOWFALL,
                    WeatherPhenomenon.HEAVY_SNOWFALL,
                    WeatherPhenomenon.LIGHT_SNOW_SHOWER,
                    WeatherPhenomenon.MODERATE_SNOW_SHOWER,
                    WeatherPhenomenon.HEAVY_SNOW_SHOWER,
                    WeatherPhenomenon.LIGHT_SLEET,
                    WeatherPhenomenon.MODERATE_SLEET)),
            Map.entry(ExtraFeeCategory.WPEF_RAIN, List.of(
                    WeatherPhenomenon.LIGHT_RAIN,
                    WeatherPhenomenon.MODERATE_RAIN,
                    WeatherPhenomenon.HEAVY_RAIN)),
            Map.entry(ExtraFeeCategory.WPEF_GLAZE_HAIL_THUNDER, List.of(
                    WeatherPhenomenon.GLAZE,
                    WeatherPhenomenon.HAIL,
                    WeatherPhenomenon.THUNDER,
                    WeatherPhenomenon.THUNDERSTORM))
    );

    @Override
    public int calculateTotalFee(Region region, Vehicle vehicle, long timestamp) throws ForbiddenVehicleTypeException {
        int regionFee = calculateRegionFee(region, vehicle);
        int extraFee = calculateExtraFee(region, vehicle, timestamp);
        return regionFee + extraFee;
    }

    private int calculateRegionFee(Region region, Vehicle vehicle) {
        return regionalFeeRepository.findCostByRegion(region, vehicle);
    }

    private int calculateExtraFee(Region region, Vehicle vehicle, long timestamp) throws ForbiddenVehicleTypeException {
        Optional<WeatherConditions> wc = weatherConditionsRepository.findOneByWeatherStationAndTimestamp(
                region.station,
                region.wmoCode,
                timestamp
        );
        if (wc.isEmpty()) {
            return 0;
        }

        WeatherConditions weatherConditions = wc.get();
        float airTemperature = weatherConditions.getAirTemperature();
        float windSpeed = weatherConditions.getWindSpeed();
        WeatherPhenomenon weatherPhenomenon = weatherConditions.getWeatherPhenomenon();

        int atefCost = getATEF(airTemperature, vehicle);
        int wsefCost = getWSEF(windSpeed, vehicle);
        int wpefCost = getWPEF(weatherPhenomenon, vehicle);

        if (isFeeCostValid(atefCost) || isFeeCostValid(wsefCost) || isFeeCostValid(wpefCost)) {
            throw new ForbiddenVehicleTypeException();
        }

        return atefCost + wsefCost + wpefCost;
    }

    private int getATEF(float airTemperature, Vehicle vehicle) {
        ExtraFeeCategory atefCategory;
        if (airTemperature > 0F) {
            return 0;
        } else if (airTemperature < -10F) {
            atefCategory = ExtraFeeCategory.ATEF_VERY_COLD;
        } else {
            atefCategory = ExtraFeeCategory.ATEF_COLD;
        }
        return extraFeeRepository.findCostByCategoryAndVehicle(atefCategory, vehicle);
    }

    private int getWSEF(float windSpeed, Vehicle vehicle) {
        ExtraFeeCategory wsefCategory;
        if (windSpeed < 10F) {
            return 0;
        } else if (windSpeed > 20F) {
            wsefCategory = ExtraFeeCategory.WSEF_VERY_FAST;
        } else {
            wsefCategory = ExtraFeeCategory.WSEF_FAST;
        }
        return extraFeeRepository.findCostByCategoryAndVehicle(wsefCategory, vehicle);
    }

    private int getWPEF(WeatherPhenomenon weatherPhenomenon, Vehicle vehicle) {
        ExtraFeeCategory wpefCategory = null;
        Set<ExtraFeeCategory> categories = wpefMap.keySet();
        for (ExtraFeeCategory category : categories) {
            List<WeatherPhenomenon> weatherPhenomena = wpefMap.get(category);
            if (weatherPhenomena.contains(weatherPhenomenon)) {
                wpefCategory = category;
            }
        }
        if (wpefCategory == null) {
            return 0;
        }
        return extraFeeRepository.findCostByCategoryAndVehicle(wpefCategory, vehicle);
    }

    private boolean isFeeCostValid(int cost) {
        return cost < 0;
    }

    @Override
    public void setRegionFee(int cost, Region region, Vehicle vehicle) {
        regionalFeeRepository.updateCostByRegionAndVehicle(cost, region, vehicle);
    }

    @Override
    public void setExtraFee(int cost, ExtraFeeCategory category, Vehicle vehicle) {
        extraFeeRepository.updateCostByCategoryAndVehicle(cost, category, vehicle);
    }

    private String readFeeFile(String filename) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(filename);
        File file = new File(resource.toURI());
        return Files.readString(file.toPath());
    }

    private Element getXMLRootElementFromData(String data) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(
                new StringReader(data)
        );
        return document.getRootElement();
    }

    /*
    * This method reads the initial regional fees file and deserializes the XML content within into
    * instances of the RegionalFee class.
    * JDOMException is thrown when the content of InitialRegionalFees.xml is invalid.
    * The other two exceptions are not expected to throw.
    * */
    @Override
    public List<RegionalFee> readRegionalFeesFromFile() throws JDOMException, URISyntaxException, IOException {
        Element root = getXMLRootElementFromData(readFeeFile(initialRegionalFeesFilename));
        return root
                .getChildren()
                .stream()
                .map(RegionalFee::new)
                .toList();
    }

    /*
     * This method reads the initial extra fees file and deserializes the XML content within into
     * instances of the ExtraFee class.
     * JDOMException is thrown when the content of InitialExtraFees.xml is invalid.
     * The other two exceptions are not expected to throw.
     * */
    @Override
    public List<ExtraFee> readExtraFeesFromFile() throws IOException, JDOMException, URISyntaxException {
        Element root = getXMLRootElementFromData(readFeeFile(initialExtraFeesFilename));
        return root
                .getChildren()
                .stream()
                .map(ExtraFee::new)
                .toList();
    }

    /*
    * This method is intended to take the list returned by readRegionalFeesFromFile
    * and insert the list's contents into the DB table.
    * */
    @Override
    public void saveRegionalFeesToRepository(List<RegionalFee> fees) {
        regionalFeeRepository.saveAllAndFlush(fees);
        logger.info("Inserted initial regional fees to database");
    }

    /*
     * This method is intended to take the list returned by readRegionalFeesFromFile
     * and insert the list's contents into the DB table.
     * */
    @Override
    public void saveExtraFeesToRepository(List<ExtraFee> fees) {
        extraFeeRepository.saveAllAndFlush(fees);
        logger.info("Inserted initial extra fees to database");
    }
}
