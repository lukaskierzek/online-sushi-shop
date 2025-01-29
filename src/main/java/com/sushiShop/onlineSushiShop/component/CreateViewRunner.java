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
        //region item_non_hidden
        //TODO: Add subcategories list to views
        String view_item_non_hidden_name = "item_non_hidden";
        String view_item_non_hidden_body = """
                SELECT
                    *
                FROM
                    ITEMS I
                    LEFT JOIN COMMENTS C ON C.COMMENTS_ID = I.ITEMS_COMMENTS_ID
                    LEFT JOIN MAIN_CATEGORIES MC ON MC.MAIN_CATEGORIES_ID = I.ITEMS_MAIN_CATEGORIES_ID
                WHERE
                    I.ITEMS_IS_HIDDEN = 0
                """;
        createView(view_item_non_hidden_name, view_item_non_hidden_body);
        //endregion

        createView("item_hidden_and_non_hidden",
                """
                        SELECT
                            *
                        FROM
                            ITEMS I
                            LEFT JOIN COMMENTS C ON C.COMMENTS_ID = I.ITEMS_COMMENTS_ID
                            LEFT JOIN MAIN_CATEGORIES MC ON MC.MAIN_CATEGORIES_ID = I.ITEMS_MAIN_CATEGORIES_ID
                        """);

        createView("main_category_non_hidden",
                """
                        SELECT
                            *
                        FROM
                            MAIN_CATEGORIES
                        WHERE
                            MAIN_CATEGORIES_IS_HIDDEN = 0
                        """);
    }
//
//    private Boolean viewExists(String viewName) {
//        String queryViewNameExists = "SELECT EXISTS (SELECT 1 FROM pg_views WHERE viewname = '%s')".formatted(viewName);
//        return jdbcTemplate.queryForObject(queryViewNameExists, Boolean.class);
//    }

    private void createView(String viewName, String viewBody) throws IllegalStateException {
        if (viewName == null) throw new IllegalStateException("viewName cannot be null");
        if (viewBody == null) throw new IllegalStateException("viewBody cannot be null");

        String viewQuery = """
                CREATE OR REPLACE VIEW %s AS
                %s
                """.formatted(viewName, viewBody);

        jdbcTemplate.execute(viewQuery);
    }
}
