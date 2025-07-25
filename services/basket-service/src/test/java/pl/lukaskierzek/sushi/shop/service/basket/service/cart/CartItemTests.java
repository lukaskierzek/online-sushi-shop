package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class CartItemTests {

    @Test
    void shouldCreateValidCartItem() {
        Money price = new Money(Currency.PLN, BigDecimal.valueOf(10));
        CartItem item = CartItem.of("product-1", 2, price);

        assertThat(item.getProductId()).isEqualTo("product-1");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getPrice()).isEqualTo(price);
    }

    @Test
    void shouldCalculatePriceCorrectly() {
        CartItem item = CartItem.of("prod", 3, new Money(Currency.PLN, BigDecimal.TEN));
        Money total = item.calculatePrice();

        assertThat(total).isEqualTo(new Money(Currency.PLN, BigDecimal.valueOf(30)));
    }

    @Test
    void shouldNotAllowEmptyProductId() {
        assertThatThrownBy(() -> CartItem.of("", 1, new Money(Currency.PLN, BigDecimal.ONE)))
            .isInstanceOf(InvalidCartException.class)
            .hasMessage("Product ID cannot be null or empty");
    }

    @Test
    void shouldNotAllowZeroOrNegativeQuantity() {
        assertThatThrownBy(() -> CartItem.of("prod", 0, new Money(Currency.PLN, BigDecimal.TEN)))
            .isInstanceOf(InvalidCartException.class)
            .hasMessage("Quantity must be greater than 0");

        assertThatThrownBy(() -> CartItem.of("prod", -1, new Money(Currency.PLN, BigDecimal.TEN)))
            .isInstanceOf(InvalidCartException.class);
    }

    @Test
    void shouldNotAllowNullPrice() {
        assertThatThrownBy(() -> CartItem.of("prod", 1, null))
            .isInstanceOf(InvalidCartException.class)
            .hasMessage("Price cannot be null");
    }

    @Test
    void shouldCompareItemsByProductId() {
        CartItem i1 = CartItem.of("prod", 1, new Money(Currency.PLN, BigDecimal.ONE));
        CartItem i2 = CartItem.of("prod", 5, new Money(Currency.PLN, BigDecimal.TEN));

        assertThat(i1).isEqualTo(i2);
        assertThat(i1.hashCode()).isEqualTo(i2.hashCode());
    }
}
