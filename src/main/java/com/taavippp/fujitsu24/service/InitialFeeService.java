package com.taavippp.fujitsu24.service;

import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.repository.ExtraFeeRepository;
import com.taavippp.fujitsu24.repository.RegionalFeeRepository;
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

/*
* This class contains every method related to inserting the constant initial fees from
* their respective .xml files.
* */
@Service
public class InitialFeeService {
    private static final Logger logger = LoggerFactory.getLogger(InitialFeeService.class);
    private static final String initialRegionalFeesFilename = "InitialRegionalFees.xml";
    private static final String initialExtraFeesFilename = "InitialExtraFees.xml";

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
    public List<RegionalFee> readInitialRegionalFeesFromXML() throws IOException, JDOMException, URISyntaxException {
        Element root = getXMLRootElementFromData(readFeeFile(initialRegionalFeesFilename));
        List<RegionalFee> fees = root
                .getChildren()
                .stream()
                .map(RegionalFee::new)
                .toList();
        return fees;
    }

    /*
     * This method reads the initial extra fees file and deserializes the XML content within into
     * instances of the ExtraFee class.
     * JDOMException is thrown when the content of InitialExtraFees.xml is invalid.
     * The other two exceptions are not expected to throw.
     * */
    public List<ExtraFee> readInitialExtraFeesFromXML() throws IOException, JDOMException, URISyntaxException {
        Element root = getXMLRootElementFromData(readFeeFile(initialExtraFeesFilename));
        List<ExtraFee> fees = root
                .getChildren()
                .stream()
                .map(ExtraFee::new)
                .toList();
        return fees;
    }

    /*
    * This method is intended to take the list returned by readInitialRegionalFeesFromXML
    * and insert the list's contents into the DB table.
    * */
    public void saveInitialRegionalFeesToRepository(List<RegionalFee> fees, RegionalFeeRepository regionalFeeRepository) {
        regionalFeeRepository.saveAllAndFlush(fees);
        logger.info("Inserted initial regional fees to database");
    }

    /*
     * This method is intended to take the list returned by readInitialExtraFeesFromXML
     * and insert the list's contents into the DB table.
     * */
    public void saveInitialExtraFeesToRepository(List<ExtraFee> fees, ExtraFeeRepository extraFeeRepository) {
        extraFeeRepository.saveAllAndFlush(fees);
        logger.info("Inserted initial extra fees to database");
    }
}
