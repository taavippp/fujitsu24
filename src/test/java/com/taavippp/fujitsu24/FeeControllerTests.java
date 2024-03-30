package com.taavippp.fujitsu24;

import com.taavippp.fujitsu24.model.ForbiddenVehicleTypeException;
import com.taavippp.fujitsu24.model.InvalidUserInputException;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.WeatherConditions.WeatherConditions;
import com.taavippp.fujitsu24.model.WeatherPhenomenon;
import com.taavippp.fujitsu24.service.WeatherService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/*
* This class contains the different tests of the FeeController endpoints.
* It makes requests to localhost:{port}/fee in different ways.
* The subclass ForbiddenVehicleTypeExceptionRequests currently fails, because it is unclear to me how to insert
* mock data to the database. However, the failing tests were later manually tested and found to work.
* */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FeeControllerTests {
        @LocalServerPort private int port;
        @Autowired private TestRestTemplate restTemplate;
        @Autowired @MockBean private WeatherService weatherService;
        private String baseURL;
        private String getFeeURL;
        private String postRegionalFeeURL;
        private String postExtraFeeURL;
        private final String invalidInputErrorMsg = new InvalidUserInputException().getMessage();
        private final String forbiddenVehicleErrorMsg = new ForbiddenVehicleTypeException().getMessage();
        private final Logger logger = LoggerFactory.getLogger(FeeControllerTests.class);

        @PostConstruct
        void init() {
            baseURL = String.format("http://localhost:%d/fee", port);
            getFeeURL = baseURL;
            postRegionalFeeURL = baseURL + "/regional";
            postExtraFeeURL = baseURL + "/extra";

            WeatherConditions wc1 = new WeatherConditions(
                    Region.TALLINN.station,
                    Region.TALLINN.wmoCode,
                    5.0f,
                    11.0f,
                    WeatherPhenomenon.CLEAR
            );
            WeatherConditions wc2 = new WeatherConditions(
                    Region.TARTU.station,
                    Region.TARTU.wmoCode,
                    -5.0f,
                    25.0f,
                    WeatherPhenomenon.FOG
            );
            WeatherConditions wc3 = new WeatherConditions(
                    Region.PARNU.station,
                    Region.PARNU.wmoCode,
                    32.0f,
                    0f,
                    WeatherPhenomenon.THUNDERSTORM
            );
            Stream<WeatherConditions> wcStream = Stream.of(wc1, wc2, wc3);
            logger.info("Inserting mock weatherconditions to DB");
            weatherService.saveWeatherData(wcStream, 0);
        }

        @Test
        void controllerWorks() {
            assertThat(this.restTemplate.getForEntity(baseURL, String.class).getBody()).isNotEmpty();
        }

        @Nested
        class NoParamRequests {
                @Test
                void getFeeRespondsWithInputError() {
                    assertThat(restTemplate.getForEntity(getFeeURL, String.class).getBody())
                            .isEqualTo(invalidInputErrorMsg);
                }

                @Test
                void postRegionalFeeRespondsWithInputError() {
                    assertThat(restTemplate.postForEntity(postRegionalFeeURL, null, String.class).getBody())
                            .isEqualTo(invalidInputErrorMsg);
                }
                @Test
                void postExtraFeeRespondsWithInputError() {
                    assertThat(restTemplate.postForEntity(postExtraFeeURL, null, String.class).getBody())
                            .isEqualTo(invalidInputErrorMsg);
                }
        }

        @Nested
        class BadParamRequests {
            @Test
            void getFeeRespondsWithInputError() {
                assertThat(restTemplate.getForEntity(
                        getFeeURL + "?city={city}&vehicle={vehicle}",
                        String.class,
                        Map.of(
                                "city", "haapsalu",
                                "vehicle", "train"))
                        .getBody())
                        .isEqualTo(invalidInputErrorMsg);
            }

            @Test
            void postRegionalFeeRespondsWithInputError() {
                assertThat(restTemplate.postForEntity(
                        postRegionalFeeURL + "?cost={cost}&city={city}&vehicle={vehicle}",
                        null,
                        String.class,
                        Map.of(
                                "cost", "50",
                                "city", "haapsalu",
                                "vehicle", "train"))
                        .getBody())
                        .isEqualTo(invalidInputErrorMsg);
            }
            @Test
            void postExtraFeeRespondsWithInputError() {
                assertThat(restTemplate.postForEntity(
                                postExtraFeeURL + "?cost={cost}&category={category}&vehicle={vehicle}",
                                null,
                                String.class,
                                Map.of(
                                        "cost", "-1",
                                        "category", "WPEF_CLEAR",
                                        "vehicle", "horse"))
                        .getBody())
                        .isEqualTo(invalidInputErrorMsg);
            }
        }

        @Nested
        class ValidParamRequests {
            @Test
            void getFeeRespondsWithCost() {
                assertThat(restTemplate.getForEntity(
                                getFeeURL + "?city={city}&vehicle={vehicle}",
                                String.class,
                                Map.of(
                                        "city", "tallinn",
                                        "vehicle", "car"))
                        .getBody())
                        .endsWith("€");
            }

            @Test
            void postRegionalFeeRespondsUpdateMessage() {
                assertThat(restTemplate.postForEntity(
                                postRegionalFeeURL + "?cost={cost}&city={city}&vehicle={vehicle}",
                                null,
                                String.class,
                                Map.of(
                                        "cost", "50",
                                        "city", "tallinn",
                                        "vehicle", "scooter"))
                        .getBody())
                        .endsWith("updated");
            }
            @Test
            void postExtraFeeRespondsWithInputError() {
                assertThat(restTemplate.postForEntity(
                                postExtraFeeURL + "?cost={cost}&category={category}&vehicle={vehicle}",
                                null,
                                String.class,
                                Map.of(
                                        "cost", "100",
                                        "category", "WSEF_FAST",
                                        "vehicle", "bike"))
                        .getBody())
                        .endsWith("updated");
            }
        }

        @Nested
        class ForbiddenVehicleTypeExceptionRequests {
            @Test
            void getFeeForWindyTartuWithBikeReturnsForbiddenVehicleError() {
                assertThat(restTemplate.getForEntity(
                                getFeeURL + "?city={city}&vehicle={vehicle}",
                                String.class,
                                Map.of(
                                        "city", "tartu",
                                        "vehicle", "bike",
                                        "timestamp", "1"))
                        .getBody())
                        .isEqualTo(forbiddenVehicleErrorMsg);
            }
            @Test
            void getFeeForThunderingParnuWithScooterReturnsForbiddenVehicleError() {
                assertThat(restTemplate.getForEntity(
                                getFeeURL + "?city={city}&vehicle={vehicle}",
                                String.class,
                                Map.of(
                                        "city", "parnu",
                                        "vehicle", "scooter",
                                        "timestamp", "1"))
                        .getBody())
                        .isEqualTo(forbiddenVehicleErrorMsg);
            }


        }
}
