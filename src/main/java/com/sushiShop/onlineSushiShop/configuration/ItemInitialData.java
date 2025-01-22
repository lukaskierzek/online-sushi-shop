package com.sushiShop.onlineSushiShop.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ItemInitialData {

    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            try (InputStream sqlInitialDataFile = getClass().getClassLoader().getResourceAsStream("initialData.sql")) {
                if (sqlInitialDataFile == null)
                    throw new IllegalStateException("File initialData.sql not found in resources folder");

                String sqlInsertIntoQuery = StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);

                jdbcTemplate.execute(sqlInsertIntoQuery);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute SQL script: %s".formatted(e.getMessage()));
            }
        };
    }
}
