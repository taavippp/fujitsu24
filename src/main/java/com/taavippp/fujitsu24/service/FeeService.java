package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import com.taavippp.fujitsu24.model.WeatherPhenomenon;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/*
* This class contains every method related to inserting the constant initial fees from
* their respective .xml files.
* */
@Service
public class FeeService implements IFeeService {
    private static final Logger logger = LoggerFactory.getLogger(FeeService.class);
    private static final String initialRegionalFeesFilename = "InitialRegionalFees.xml";
    private static final String initialExtraFeesFilename = "InitialExtraFees.xml";

    @Override
    public int calculateTotalFee(Region region, Vehicle vehicle, long timestamp,
                                 RegionalFeeRepository regionalFeeRepository, ExtraFeeRepository extraFeeRepository) {
        return 0;
    }

    @Override
    public int calculateRegionFee(Region region, RegionalFeeRepository repository) {
        return repository.findCostByRegion(region);
    }

    @Override
    public int calculateExtraFee(Region region, Vehicle vehicle, long timestamp,
                                 WeatherConditionsRepository weatherConditionsRepository, ExtraFeeRepository extraFeeRepository) {
        Optional<WeatherConditions> optionalWC = weatherConditionsRepository.findOneByWeatherStationAndTimestamp(
                region.station,
                region.wmoCode,
                timestamp
        );
        if (optionalWC.isEmpty()) {
            return 0;
        }
        WeatherConditions weatherConditions = optionalWC.get();

        int totalExtraFee = 0;
        float airTemperature = weatherConditions.getAirTemperature();
        float windSpeed = weatherConditions.getWindSpeed();
        WeatherPhenomenon weatherPhenomenon = weatherConditions.getWeatherPhenomenon();
        if (airTemperature < 0F) {
            ExtraFeeCategory ATEFCategory;
            if (airTemperature < -10F) {
                ATEFCategory = ExtraFeeCategory.ATEF_VERY_COLD;
            } else {
                ATEFCategory = ExtraFeeCategory.ATEF_COLD;
            }
            totalExtraFee += extraFeeRepository.findCostByCategoryAndVehicle(ATEFCategory, vehicle);
        }
        return totalExtraFee;
    }

    @Override
    public void setRegionFeeCost(Region region, int cost) {

    }

    @Override
    public void setExtraFeeCost(ExtraFeeCategory category, Vehicle vehicle, int cost) {

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
    * This method is intended to take the list returned by readInitialRegionalFeesFromXML
    * and insert the list's contents into the DB table.
    * */
    @Override
    public void saveRegionalFeesToRepository(List<RegionalFee> fees, RegionalFeeRepository repository) {
        repository.saveAllAndFlush(fees);
        logger.info("Inserted initial regional fees to database");
    }

    /*
     * This method is intended to take the list returned by readInitialExtraFeesFromXML
     * and insert the list's contents into the DB table.
     * */
    @Override
    public void saveExtraFeesToRepository(List<ExtraFee> fees, ExtraFeeRepository repository) {
        repository.saveAllAndFlush(fees);
        logger.info("Inserted initial extra fees to database");
    }
}
