package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import lombok.experimental.UtilityClass;

@UtilityClass
class ProductFixture {

    static final String EXPECTED_VALIDATION_ERROR_RESULT = """
            {
              "errors" : [ {
                "field" : "description",
                "message" : "Description must be at least 10 characters"
              }, {
                "field" : "price",
                "message" : "Price must be greater than 0"
              }, {
                "field" : "name",
                "message" : "Name must be at least 3 characters"
              } ]
            }
            """;
}
