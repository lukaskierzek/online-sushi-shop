package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartTests {

    private final OwnerId validOwner = new OwnerId("user123", "anon456");
    private final CartItem item1 = new CartItem("prod-1", 2, new Money(Currency.PLN, new BigDecimal("10.00")));
    private final CartItem item2 = new CartItem("prod-2", 1, new Money(Currency.PLN, new BigDecimal("15.00")));

    @Test
    void shouldCreateCartWithValidOwner() {
        Cart cart = Cart.newCart(validOwner);
        assertEquals(validOwner, cart.getOwnerId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void shouldThrowWhenOwnerIdIsNull() {
        assertThrows(InvalidCartException.class, () -> Cart.newCart(null));
    }

    @Test
    void shouldAddItemToCart() {
        Cart cart = Cart.newCart(validOwner);
        cart.addItem(item1);
        assertTrue(cart.getItems().contains(item1));
        assertEquals(1, cart.getEvents().size());
    }

    @Test
    void shouldThrowWhenAddingDuplicateItem() {
        Cart cart = Cart.newCart(validOwner);
        cart.addItem(item1);
        assertThrows(InvalidCartItemException.class, () -> cart.addItem(item1));
    }

    @Test
    void shouldReplaceItemsInCart() {
        Cart cart = Cart.newCart(validOwner);
        cart.addItem(item1);

        Set<CartItem> newItems = Set.of(item2);
        cart.replaceItems(newItems);

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item2));
        assertEquals(3, cart.getEvents().size()); // item1 added, items removed, item2 added
    }

    @Test
    void shouldCalculateTotalPrice() {
        Cart cart = Cart.newCart(validOwner);
        cart.addItem(item1); // 2 x 10 = 20
        cart.addItem(item2); // 1 x 15 = 15

        Money total = cart.calculateTotalPrice();
        assertEquals(new Money(Currency.PLN, new BigDecimal("35.00")), total);
    }

    @Test
    void shouldReturnZeroPriceForEmptyCart() {
        Cart cart = Cart.newCart(validOwner);
        assertEquals(new Money(Currency.PLN, BigDecimal.ZERO), cart.calculateTotalPrice());
    }

    @Test
    void shouldClearEvents() {
        Cart cart = Cart.newCart(validOwner);
        cart.addItem(item1);
        cart.clearEvents();
        assertTrue(cart.getEvents().isEmpty());
    }
}
