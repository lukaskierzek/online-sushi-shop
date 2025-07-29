package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;

@UtilityClass
class CartFixture {

    static final String UPDATE_CART_REQUEST = """
        {
            "items": [{
                "id": "prod-1",
                "quantity": 1
            },
            {
                "id": "prod-2",
                "quantity": 2
            },
            {
                "id": "prod-3",
                "quantity": 3
            },
            {
                "id": "prod-4",
                "quantity": 4
            }]
        }
        """;
}
