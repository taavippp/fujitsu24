package com.taavippp.fujitsu24.controller;

import com.taavippp.fujitsu24.config.WeatherJobConfig;
import com.taavippp.fujitsu24.model.*;
import com.taavippp.fujitsu24.service.FeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/*
* Every method of this class takes user input from various endpoints, validates them and transforms them and then lets the FeeService
* class operate with them.
* The available endpoints are:
* /fee (GET request)
* /fee/regional (POST request)
* /fee/extra (POST request)
* By default, the project is hosted at localhost:8080, so for example, one would send a request to the /fee endpoint
* by using the url http://localhost:8080/fee
* */
@RestController
@RequestMapping(FeeController.path)
public class FeeController {
    protected static final String path = "/fee";
    private @Autowired FeeService feeService;
    private final Logger logger = LoggerFactory.getLogger(FeeController.class);

    private final int EURO = 100;

    // This method is used when GET request parameters are missing from the request.
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<String> handleMissingArguments() {
        return new ResponseEntity<>(new InvalidUserInputException().getMessage(), HttpStatus.BAD_REQUEST);
    }

    /* http://localhost:8080/fee GET
    * Returns the delivery fee in euros depending on the following request parameters:
    * city - string, value from {tallinn, tartu, parnu} (can use any case)
    * vehicle - string, value from {car, scooter, bike} (can use any case)
    * timestamp - optional integer, must be epoch time
    * */
    @GetMapping()
    public ResponseEntity<String> getDeliveryFee(
            @RequestParam(name = "city") String regionStr,
            @RequestParam(name = "vehicle") String vehicleStr,
            @RequestParam(name = "timestamp", required = false) String timestampStr
    ) {
        logger.info("STARTING /fee GET REQUEST");

        Region region;
        Vehicle vehicle;
        Long timestamp = null;

        try {
            region = Region.valueOf(regionStr.toUpperCase());
            vehicle = Vehicle.valueOf(vehicleStr.toUpperCase());
            if (timestampStr != null) {
                timestamp = Long.parseLong(timestampStr);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new InvalidUserInputException().getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (timestamp == null) {
            timestamp = Instant.now().getEpochSecond();
        }

        try {
            long lastExecution = WeatherJobConfig.getLastExecutionBefore(timestamp);
            int fee = feeService.calculateTotalFee(region, vehicle, lastExecution);

            int euros = fee / EURO;
            int cents = fee % EURO;

            return new ResponseEntity<>(
                    String.format("%d.%02d €", euros, cents),
                    HttpStatus.OK
            );
        } catch (ForbiddenVehicleTypeException e) {
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /* http://localhost:8080/fee/regional POST
     * Sets the regional fee of a vehicle depending on the following parameters:
     * cost - integer number, must be in euro cents (100 means 1.00 EUR)
     * city - string, value from {tallinn, tartu, parnu} (can use any case)
     * vehicle - string, value from {car, scooter, bike} (can use any case)
     * */
    @PostMapping(value = "/regional")
    public ResponseEntity<String> setRegionalFee(
            @RequestParam(name = "cost") String costStr,
            @RequestParam(name = "city") String regionStr,
            @RequestParam(name = "vehicle") String vehicleStr
    ) {
        logger.info("STARTING /fee/regional POST REQUEST");

        int cost;
        Region region;
        Vehicle vehicle;

        try {
            cost = Integer.parseInt(costStr);
            region = Region.valueOf(regionStr.toUpperCase());
            vehicle = Vehicle.valueOf(vehicleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new InvalidUserInputException().getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (cost < 0) {
            return new ResponseEntity<>(
                    new InvalidUserInputException().getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
        
        feeService.setRegionFee(cost, region, vehicle);
        return new ResponseEntity<>(
                String.format("Delivery fee for %s in %s updated", vehicle.name(), region.name()),
                HttpStatus.OK
        );
    }

    /* http://localhost:8080/fee/regional POST
     * Sets the regional fee of a vehicle depending on the following parameters:
     * cost - integer number, must be in euro cents (100 means 1.00 EUR)
     * category - string, value from ExtraFeeCategory class (can use any case, underscores (_) must
     *      be included in the name)
     * vehicle - string, value from {car, scooter, bike} (can use any case)
     * */
    @PostMapping(value = "/extra")
    public ResponseEntity<String> setExtraFee(
            @RequestParam(name = "cost") String costStr,
            @RequestParam(name = "category") String categoryStr,
            @RequestParam(name = "vehicle") String vehicleStr
    ) {
        logger.info("STARTING /fee/extra POST REQUEST");

        int cost;
        ExtraFeeCategory category;
        Vehicle vehicle;

        try {
            cost = Integer.parseInt(costStr);
            category = ExtraFeeCategory.valueOf(categoryStr.toUpperCase());
            vehicle = Vehicle.valueOf(vehicleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new InvalidUserInputException().getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (cost < 0) {
            return new ResponseEntity<>("Invalid input", HttpStatus.BAD_REQUEST);
        }

        feeService.setExtraFee(cost, category, vehicle);
        return new ResponseEntity<>(
                String.format("Delivery fee for %s in category %s updated", vehicle.name(), category.name()),
                HttpStatus.OK
        );
    }
}
