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
import java.util.ArrayList;
import java.util.function.Function;

@Component
public class ItemInitialData {
    Function<InputStream, String> getStringFromSQLFile = (sqlInitialDataFile) -> {
        try {
            return StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed copy to string function. %s".formatted(e.getMessage()));
        }
    };

    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            var sqlFiles = new ArrayList<String>();
            sqlFiles.add("initialData.sql");

            for (var file : sqlFiles)
                saveSQLFilesToDabatase(jdbcTemplate, file);
        };
    }

    private void saveSQLFilesToDabatase(JdbcTemplate jdbcTemplate, String initialFile) {
        try (InputStream sqlInitialDataFile = getClass().getClassLoader().getResourceAsStream(initialFile)) {
            if (sqlInitialDataFile == null)
                throw new PostgresSQLNotFoundException("File %s not found in resources folder".formatted(initialFile));

            String sqlInsertIntoQuery = getStringFromSQLFile.apply(sqlInitialDataFile);

            jdbcTemplate.execute(sqlInsertIntoQuery);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL script: %s".formatted(e.getMessage()));
        }
    }

//    @NotNull
//    private static String getStringFromSQLFile(InputStream sqlInitialDataFile) throws IOException {
//        return StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);
//    }
}
