package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice());

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().stream().anyMatch(i -> i.productId().equals(item1.productId())));
        assertEquals(1, cart.getEvents().size());
    }

    @Test
    void shouldThrowWhenAddingDuplicateItem() {
        Cart cart = Cart.newCart(validOwner);
        cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice());

        assertThrows(InvalidCartItemException.class, () ->
            cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice()));
    }

    @Test
    void shouldRemoveItemFromCart() {
        Cart cart = Cart.newCart(validOwner);
        cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice());

        cart.removeCartItem(item1.productId());

        assertTrue(cart.getItems().isEmpty());
        assertEquals(2, cart.getEvents().size()); // added + removed
    }

    @Test
    void shouldThrowWhenRemovingNonExistentItem() {
        Cart cart = Cart.newCart(validOwner);
        assertThrows(InvalidCartItemException.class, () -> cart.removeCartItem("not-existing-id"));
    }

    @Test
    void shouldUpdateQuantity() {
        Cart cart = Cart.newCart(validOwner);
        cart.addCartItem(item1.productId(), 1, item1.unitPrice()); // initial quantity = 1

        cart.updateCartItemQuantity(item1.productId(), 3); // update to 3

        var updatedItem = cart.getItems().stream()
            .filter(i -> i.productId().equals(item1.productId()))
            .findFirst()
            .orElseThrow();

        assertEquals(3, updatedItem.quantity());
        assertEquals(1, cart.getEvents().size()); // added
    }

    @Test
    void shouldThrowWhenUpdatingQuantityToInvalidValue() {
        Cart cart = Cart.newCart(validOwner);
        cart.addCartItem(item1.productId(), 1, item1.unitPrice());

        assertThrows(InvalidCartItemException.class, () ->
            cart.updateCartItemQuantity(item1.productId(), 0)); // invalid quantity
    }

    @Test
    void shouldCalculateTotalPrice() {
        Cart cart = Cart.newCart(validOwner);
        cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice()); // 2 x 10 = 20
        cart.addCartItem(item2.productId(), item2.quantity(), item2.unitPrice()); // 1 x 15 = 15

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
        cart.addCartItem(item1.productId(), item1.quantity(), item1.unitPrice());
        assertFalse(cart.getEvents().isEmpty());

        cart.clearEvents();

        assertTrue(cart.getEvents().isEmpty());
    }
}
