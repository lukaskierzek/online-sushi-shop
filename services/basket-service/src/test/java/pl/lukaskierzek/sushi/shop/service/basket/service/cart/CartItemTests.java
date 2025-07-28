package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTests {

    @Test
    void shouldCreateCartItemWithValidFields() {
        CartItem item = new CartItem("product-123", 2, new Money(Currency.PLN, new BigDecimal("10.00")));

        assertEquals("product-123", item.productId());
        assertEquals(2, item.quantity());
        assertEquals(new BigDecimal("10.00"), item.unitPrice().amount());
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem(null, 1, new Money(Currency.PLN, new BigDecimal("5.00")));
        });

        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsEmpty() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem("", 1, new Money(Currency.PLN, new BigDecimal("5.00")));
        });

        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNull() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem("product-123", null, new Money(Currency.PLN, new BigDecimal("5.00")));
        });

        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem("product-123", 0, new Money(Currency.PLN, new BigDecimal("5.00")));
        });

        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem("product-123", -5, new Money(Currency.PLN, new BigDecimal("5.00")));
        });

        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            new CartItem("product-123", 1, null);
        });

        assertEquals("Price cannot be null", exception.getMessage());
    }

    @Test
    void shouldCalculateTotalPriceCorrectly() {
        Money unitPrice = new Money(Currency.EUR, new BigDecimal("19.99"));
        CartItem item = new CartItem("item-001", 3, unitPrice);

        Money total = item.calculatePrice();

        assertEquals(Currency.EUR, total.currency());
        assertEquals(new BigDecimal("59.97"), total.amount());
    }

    @Test
    void shouldBeEqualIfProductIdIsSame() {
        CartItem item1 = new CartItem("prod-001", 1, new Money(Currency.USD, new BigDecimal("5.00")));
        CartItem item2 = new CartItem("prod-001", 5, new Money(Currency.USD, new BigDecimal("5.00")));

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void shouldNotBeEqualIfProductIdIsDifferent() {
        CartItem item1 = new CartItem("prod-001", 1, new Money(Currency.USD, new BigDecimal("5.00")));
        CartItem item2 = new CartItem("prod-002", 1, new Money(Currency.USD, new BigDecimal("5.00")));

        assertNotEquals(item1, item2);
    }

    @Test
    void shouldBehaveCorrectlyInHashSet() {
        Set<CartItem> set = new HashSet<>();
        set.add(new CartItem("prod-001", 1, new Money(Currency.USD, new BigDecimal("5.00"))));
        set.add(new CartItem("prod-001", 5, new Money(Currency.USD, new BigDecimal("5.00")))); // duplicate by ID

        assertEquals(1, set.size());
    }
}
