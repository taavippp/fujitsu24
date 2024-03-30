package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.*;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/*
* Due to the size of the FeeService class, this interface was made to ensure every necessary method is created.
* */
public interface IFeeService {
    int calculateTotalFee(Region region, Vehicle vehicle, long timestamp) throws ForbiddenVehicleTypeException;
    void setRegionFee(int cost, Region region, Vehicle vehicle);
    void setExtraFee(int cost, ExtraFeeCategory category, Vehicle vehicle);

    List<RegionalFee> readRegionalFeesFromFile() throws JDOMException, URISyntaxException, IOException;
    List<ExtraFee> readExtraFeesFromFile() throws IOException, JDOMException, URISyntaxException;
    void saveRegionalFeesToRepository(List<RegionalFee> fees);
    void saveExtraFeesToRepository(List<ExtraFee> fees);
}
