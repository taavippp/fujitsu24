package com.taavippp.fujitsu24.config;

import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import com.taavippp.fujitsu24.service.FeeService;
import jakarta.annotation.PostConstruct;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/*
When the application is initialized, this class inserts the original fees into the database.
* */
@Component
public class FeeServiceConfig {
    private @Autowired FeeService feeService;

    @PostConstruct
    public void insertInitialFees() throws IOException, URISyntaxException, JDOMException {
        saveInitialFeesToRepositories();
    }

    private void saveInitialFeesToRepositories() throws URISyntaxException, IOException, JDOMException {
        List<RegionalFee> regionalFees = feeService.readRegionalFeesFromFile();
        feeService.saveRegionalFeesToRepository(regionalFees);
        List<ExtraFee> extraFees = feeService.readExtraFeesFromFile();
        feeService.saveExtraFeesToRepository(extraFees);
    }
}
