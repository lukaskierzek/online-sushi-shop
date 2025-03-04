package com.sushiShop.onlineSushiShop.configuration;

import com.sushiShop.onlineSushiShop.exception.PostgresSQLNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ItemInitialData {

    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            String initialFile = "initialData.sql";
            try (InputStream sqlInitialDataFile = getClass().getClassLoader().getResourceAsStream(initialFile)) {
                if (sqlInitialDataFile == null)
                    throw new PostgresSQLNotFoundException("File " + initialFile + " not found in resources folder");

                String sqlInsertIntoQuery = getStringFromSQLFile(sqlInitialDataFile);

                jdbcTemplate.execute(sqlInsertIntoQuery);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute SQL script: %s".formatted(e.getMessage()));
            }
        };
    }

    @NotNull
    private static String getStringFromSQLFile(InputStream sqlInitialDataFile) throws IOException {
        return StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);
    }
}
