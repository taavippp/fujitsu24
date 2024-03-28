package com.taavippp.fujitsu24.config;

import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.service.FeeService;
import jakarta.annotation.PostConstruct;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/*
When the application is initialized, this class inserts the original fees into the database.
* */
@Component
public class InitialFeeConfig {
    @Autowired RegionalFeeRepository regionalFeeRepository;
    @Autowired ExtraFeeRepository extraFeeRepository;
    FeeService initialFeeService = new FeeService();

    /*
    This method collects the initial regional fees and extra fees and inserts them into the database
    through the FeeService class.
    * */
    @PostConstruct
    public void insertInitialFeesToDatabase() throws IOException, URISyntaxException, JDOMException {
        List<RegionalFee> regionalFees = initialFeeService.readRegionalFeesFromFile();
        initialFeeService.saveRegionalFeesToRepository(regionalFees, regionalFeeRepository);
        List<ExtraFee> extraFees = initialFeeService.readExtraFeesFromFile();
        initialFeeService.saveExtraFeesToRepository(extraFees, extraFeeRepository);
    }
}
