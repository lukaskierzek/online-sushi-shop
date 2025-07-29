package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.*;

class CartItemTests {

    @Test
    void shouldCreateValidCartItem() {
        CartItem item = new CartItem("prod-1", 2, new Money(Currency.PLN, new BigDecimal("10.00")));
        assertEquals("prod-1", item.productId());
    }

    @Test
    void shouldThrowForEmptyProductId() {
        assertThrows(InvalidCartItemException.class, () -> new CartItem("", 2, new Money(Currency.PLN, BigDecimal.TEN)));
    }

    @Test
    void shouldThrowForInvalidQuantity() {
        assertThrows(InvalidCartItemException.class, () -> new CartItem("prod", 0, new Money(Currency.PLN, BigDecimal.TEN)));
    }

    @Test
    void shouldThrowForNullPrice() {
        assertThrows(InvalidCartItemException.class, () -> new CartItem("prod", 1, null));
    }

    @Test
    void shouldCalculatePriceCorrectly() {
        CartItem item = new CartItem("prod", 3, new Money(Currency.PLN, new BigDecimal("5.00")));
        Money total = item.calculatePrice();
        assertEquals(new Money(Currency.PLN, new BigDecimal("15.00")), total);
    }

    @Test
    void shouldCompareCartItemsByProductIdOnly() {
        CartItem item1 = new CartItem("prod-1", 1, new Money(Currency.PLN, BigDecimal.TEN));
        CartItem item2 = new CartItem("prod-1", 99, new Money(Currency.PLN, new BigDecimal("999.99")));

        assertEquals(item1, item2);
    }
}
