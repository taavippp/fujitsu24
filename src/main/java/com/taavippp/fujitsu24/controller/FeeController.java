package com.taavippp.fujitsu24.controller;

import com.taavippp.fujitsu24.config.WeatherJobConfig;
import com.taavippp.fujitsu24.job.WeatherJob;
import com.taavippp.fujitsu24.model.ForbiddenVehicleTypeException;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import com.taavippp.fujitsu24.repository.WeatherConditionsRepository;
import com.taavippp.fujitsu24.service.FeeService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam() String city,
            @RequestParam() String vehicle,
            @RequestParam(required = false) Long timestamp
    ) {
        logger.info("STARTING /fee GET REQUEST");
        Region r = Region.valueOf(city.toUpperCase());
        Vehicle v = Vehicle.valueOf(vehicle.toUpperCase());
        if (timestamp == null) {
            timestamp = Instant.now().getEpochSecond();
        }
        try {
            long lastExecution = WeatherJobConfig.getLastExecutionBefore(timestamp);
            int fee = feeService.calculateTotalFee(r, v, lastExecution);

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
}
