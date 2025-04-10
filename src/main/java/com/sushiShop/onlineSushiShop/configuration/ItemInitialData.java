package com.sushiShop.onlineSushiShop.configuration;

import com.sushiShop.onlineSushiShop.enums.Database;
import com.sushiShop.onlineSushiShop.enums.Role;
import com.sushiShop.onlineSushiShop.exception.PostgresSQLNotFoundException;
import com.sushiShop.onlineSushiShop.model.User;
import com.sushiShop.onlineSushiShop.model.UserBuilder;
import com.sushiShop.onlineSushiShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ItemInitialData {
    private DataSource dataSource;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ItemInitialData(DataSource dataSource, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    Function<InputStream, String> getStringFromSQLFile = (sqlInitialDataFile) -> {
        try {
            return StreamUtils.copyToString(sqlInitialDataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed copy to string function. %s".formatted(e.getMessage()));
        }
    };

    Supplier<String> printCurrentDatabase = () -> {
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

            createAdminUser();
            createUserUser();
        };
    }

    private void createAdminUser() {
        String adminName = "admin";
        String adminPassword = "admin123";

        if (userRepository.findByUserName(adminName).isEmpty()) {
            User user = new UserBuilder()
                .setUserName("admin")
                .setUserEmail("admin@admin.com")
                .setUserPassword(passwordEncoder.encode(adminPassword))
                .setUserRole(Role.ADMIN).setUserIsActive(true)
                .createUser();
            userRepository.save(user);
        }
    }

    private void createUserUser() {
        if (userRepository.findByUserName("user").isEmpty()) {
            User user = new UserBuilder()
                .setUserName("user")
                .setUserEmail("user@user.com")
                .setUserPassword(passwordEncoder.encode("user123"))
                .setUserRole(Role.USER).setUserIsActive(true)
                .createUser();
            userRepository.save(user);
        }
    }

    private void saveSQLFilesToDabatase(JdbcTemplate jdbcTemplate, String sqlInitialFile) {
        try (InputStream sqlInitialDataFile = getClass().getClassLoader().getResourceAsStream(sqlInitialFile)) {
            if (sqlInitialDataFile == null)
                throw new PostgresSQLNotFoundException("File %s not found in resources folder".formatted(sqlInitialFile));

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

//TODO: Check later
