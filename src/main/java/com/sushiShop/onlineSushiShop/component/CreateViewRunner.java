package com.sushiShop.onlineSushiShop.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateViewRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        String ViewItemNonHiddenQuery = """
                CREATE VIEW item_non_hidden AS
                SELECT
                    *
                FROM
                    ITEMS I
                    LEFT JOIN COMMENTS C ON C.COMMENTS_ID = I.ITEMS_COMMENTS_ID
                    LEFT JOIN MAIN_CATEGORIES MC ON MC.MAIN_CATEGORIES_ID = I.ITEMS_MAIN_CATEGORIES_ID
                WHERE
                    I.ITEMS_IS_HIDDEN = 0
                """;

        String checkIfViewViewItemNonHiddenExists = "SELECT EXISTS (SELECT 1 FROM pg_views WHERE viewname = 'item_non_hidden')";

        Boolean ifViewExistsQuery = jdbcTemplate.queryForObject(checkIfViewViewItemNonHiddenExists, Boolean.class);

        if (Boolean.FALSE.equals(ifViewExistsQuery))
            jdbcTemplate.execute(ViewItemNonHiddenQuery);
    }
}
