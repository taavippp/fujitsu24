package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

// TODO: needs extra methods for determining each extra fee category
// TODO: validate extra fee (if it's -1, then “Usage of selected vehicle type is forbidden”)
public interface IFeeService {
    int calculateTotalFee(Region region, Vehicle vehicle, long timestamp,
                          RegionalFeeRepository regionalFeeRepository, ExtraFeeRepository extraFeeRepository);
    int calculateRegionFee(Region region, RegionalFeeRepository repository);
    int calculateExtraFee(Region region, Vehicle vehicle, long timestamp,
                          WeatherConditionsRepository weatherConditionsRepository, ExtraFeeRepository extraFeeRepository);
    void setRegionFeeCost(Region region, int cost);
    void setExtraFeeCost(ExtraFeeCategory category, Vehicle vehicle, int cost);

    List<RegionalFee> readRegionalFeesFromFile() throws JDOMException, URISyntaxException, IOException;
    List<ExtraFee> readExtraFeesFromFile() throws IOException, JDOMException, URISyntaxException;
    void saveRegionalFeesToRepository(List<RegionalFee> fees, RegionalFeeRepository repository);
    void saveExtraFeesToRepository(List<ExtraFee> fees, ExtraFeeRepository repository);
}
