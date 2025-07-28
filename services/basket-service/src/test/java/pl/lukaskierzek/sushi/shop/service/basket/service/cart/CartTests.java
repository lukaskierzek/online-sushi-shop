package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CartTests {

    private Cart cart;

    @BeforeEach
    void setup() {
        cart = Cart.newCart("user-123");
    }

    @Test
    void shouldCreateNewCartWithValidUserId() {
        Cart cart = Cart.newCart("user-123");

        assertNotNull(cart.getId());
        assertEquals("user-123", cart.getUserId());
        assertTrue(cart.getItems().isEmpty());
        assertTrue(cart.getEvents().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            Cart.newCart(null);
        });

        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsEmpty() {
        Exception exception = assertThrows(InvalidCartException.class, () -> {
            Cart.newCart("");
        });

        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldAddItemToCart() {
        CartItem item = new CartItem("prod-001", 2, new Money(Currency.PLN, new BigDecimal("10.00")));

        cart.addItem(item);

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item));

        // Check event
        assertEquals(1, cart.getEvents().size());
        assertTrue(cart.getEvents().stream().anyMatch(e -> e instanceof DomainEvent.CartItemAddedEvent));
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateItem() {
        CartItem item1 = new CartItem("prod-001", 2, new Money(Currency.PLN, new BigDecimal("10.00")));
        CartItem item2 = new CartItem("prod-001", 1, new Money(Currency.PLN, new BigDecimal("15.00")));

        cart.addItem(item1);

        Exception exception = assertThrows(InvalidCartItemException.class, () -> {
            cart.addItem(item2);
        });

        assertEquals("Cart item already added", exception.getMessage());
    }

    @Test
    void shouldReplaceItemsCorrectly() {
        CartItem oldItem = new CartItem("old", 1, new Money(Currency.PLN, new BigDecimal("5.00")));
        CartItem newItem1 = new CartItem("new1", 2, new Money(Currency.PLN, new BigDecimal("3.00")));
        CartItem newItem2 = new CartItem("new2", 1, new Money(Currency.PLN, new BigDecimal("7.00")));

        cart.addItem(oldItem);
        cart.clearEvents(); // ignore previous add event

        cart.replaceItems(Set.of(newItem1, newItem2));

        Set<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertTrue(items.contains(newItem1));
        assertTrue(items.contains(newItem2));
        assertFalse(items.contains(oldItem));

        // Check events
        assertEquals(3, cart.getEvents().size()); // 1 removed + 2 added
        assertTrue(cart.getEvents().stream().anyMatch(e -> e instanceof DomainEvent.CartItemsRemovedEvent));
    }

    @Test
    void shouldCalculateTotalPriceCorrectly() {
        cart.addItem(new CartItem("p1", 1, new Money(Currency.PLN, new BigDecimal("10.00"))));
        cart.addItem(new CartItem("p2", 2, new Money(Currency.PLN, new BigDecimal("5.00"))));

        Money total = cart.calculateTotalPrice();

        assertEquals(Currency.PLN, total.currency());
        assertEquals(new BigDecimal("20.00"), total.amount());
    }

    @Test
    void shouldReturnZeroPriceForEmptyCart() {
        Money total = cart.calculateTotalPrice();

        assertEquals(Currency.PLN, total.currency());
        assertEquals(BigDecimal.ZERO, total.amount());
    }

    @Test
    void shouldClearEvents() {
        CartItem item = new CartItem("item", 1, new Money(Currency.PLN, new BigDecimal("5.00")));
        cart.addItem(item);

        assertFalse(cart.getEvents().isEmpty());

        cart.clearEvents();

        assertTrue(cart.getEvents().isEmpty());
    }

    @Test
    void shouldReturnUnmodifiableItemsAndEvents() {
        assertThrows(UnsupportedOperationException.class, () -> {
            cart.getItems().add(new CartItem("id", 1, new Money(Currency.PLN, BigDecimal.ONE)));
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            cart.getEvents().add(new DomainEvent() {
            });
        });
    }
}
