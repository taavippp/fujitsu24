package com.taavippp.fujitsu24.controller;

import com.taavippp.fujitsu24.config.WeatherJobConfig;
import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.ForbiddenVehicleTypeException;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import com.taavippp.fujitsu24.service.FeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping(FeeController.path)
public class FeeController {
    public static final String path = "/fee";
    private @Autowired FeeService feeService;
    private final Logger logger = LoggerFactory.getLogger(FeeController.class);

    private final int EURO = 100;

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
                    "Invalid input",
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

    @PostMapping(value = "/regional", consumes = MediaType.APPLICATION_JSON_VALUE)
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
                    "Invalid input",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (cost < 0) {
            return new ResponseEntity<>("Invalid input", HttpStatus.BAD_REQUEST);
        }
        
        feeService.setRegionFee(cost, region, vehicle);
        return new ResponseEntity<>(
                String.format("Delivery fee for %s in %s updated", vehicle.name(), region.name()),
                HttpStatus.OK
        );
    }

    @PostMapping(value = "/extra", consumes = MediaType.APPLICATION_JSON_VALUE)
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
                    "Invalid input",
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
