package com.sushiShop.onlineSushiShop.configuration;

import com.sushiShop.onlineSushiShop.enums.Database;
import com.sushiShop.onlineSushiShop.exception.PostgresSQLNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ItemInitialData {
    @Autowired
    private DataSource dataSource;

    Function<InputStream, String> getStringFromSQLFile = (sqlInitialDataFile) -> {
        try {
            return StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed copy to string function. %s".formatted(e.getMessage()));
        }
    };

    Supplier<String> printCurrentDatabase = () ->{
        try (Connection connection = dataSource.getConnection()) {
            String currDb = connection.getCatalog();
            return currDb;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };



    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            String resultCurrDb = printCurrentDatabase.get();

            HashMap<String, String> sqlFiles = new HashMap<>();
            sqlFiles.put("SqlFileInitialDataForTests", "InitialDataForTests.sql");
            sqlFiles.put("SqlFileInitialData", "initialData.sql");

            if (resultCurrDb.equals(Database.POSTGRES_ONLINESUSHISHOP_TEST.getDatabaseName()))
                saveSQLFilesToDabatase(jdbcTemplate, sqlFiles.get("SqlFileInitialDataForTests"));
            else
                saveSQLFilesToDabatase(jdbcTemplate, sqlFiles.get("SqlFileInitialData"));
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
