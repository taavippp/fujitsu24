package com.taavippp.fujitsu24.config;

import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
import com.taavippp.fujitsu24.service.InitialFeeService;
import jakarta.annotation.PostConstruct;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/*
When the application is initialized, this class inserts the constant base fees into the database.
* */
@Component
public class InitialFeeConfig {
    @Autowired RegionalFeeRepository regionalFeeRepository;
    @Autowired ExtraFeeRepository extraFeeRepository;
    InitialFeeService initialFeeService = new InitialFeeService();

    /*
    This method collects the initial regional fees and extra fees and inserts them into the database
    through the InitialFeeService class.
    * */
    @PostConstruct
    public void insertInitialFeesToDatabase() throws IOException, URISyntaxException, JDOMException {
        List<RegionalFee> regionalFees = initialFeeService.readInitialRegionalFeesFromXML();
        initialFeeService.saveInitialRegionalFeesToRepository(regionalFees, regionalFeeRepository);
        List<ExtraFee> extraFees = initialFeeService.readInitialExtraFeesFromXML();
        initialFeeService.saveInitialExtraFeesToRepository(extraFees, extraFeeRepository);
    }
}
