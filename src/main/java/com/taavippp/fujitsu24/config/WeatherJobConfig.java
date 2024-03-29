package com.taavippp.fujitsu24.config;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class WeatherJobConfig {
    private static final String timezone = "GMT+2";

    private static final String devCron = "0 * * * * *";
    private static final String prodCron = "0 15 * * * *";
    public static final String cronExpression = devCron;

    private final static CronDefinition cronDefinition = CronDefinitionBuilder.defineCron()
            .withSeconds().and()
            .withMinutes().and()
            .withHours().and()
            .withDayOfMonth().and()
            .withMonth().and()
            .withDayOfWeek().and().instance();
    private static final CronParser cronParser = new CronParser(cronDefinition);

    public static long getLastExecutionBefore(long timestamp) {
        Cron cron = cronParser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime time = ZonedDateTime.ofInstant(instant, ZoneId.of(timezone));

        Optional<ZonedDateTime> lastExecution = executionTime.lastExecution(time);
        if (lastExecution.isEmpty()) {
            return 0;
        }
        return lastExecution.get().toEpochSecond();
    }
}
