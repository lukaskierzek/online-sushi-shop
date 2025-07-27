package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CartTests {

    @Test
    void shouldCreateNewCartWithValidUserId() {
        Cart cart = Cart.newCart("user-123");

        assertThat(cart.getUserId()).isEqualTo("user-123");
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getId()).isNotBlank();
    }

    @Test
    void shouldNotCreateCartWithEmptyUserId() {
        assertThatThrownBy(() -> Cart.newCart(""))
            .isInstanceOf(InvalidCartException.class)
            .hasMessage("User ID cannot be null or empty");
    }

    @Test
    void shouldAddItemToCart() {
        Cart cart = Cart.newCart("user-1");
        CartItem item = new CartItem("prod-1", 2, new Money(Currency.PLN, BigDecimal.valueOf(10)));

        cart.addItem(item);

        assertThat(cart.getItems()).containsExactly(item);
    }

    @Test
    void shouldNotAddDuplicateItem() {
        Cart cart = Cart.newCart("user-1");
        CartItem item1 = new CartItem("prod-1", 1, new Money(Currency.PLN, BigDecimal.TEN));
        CartItem item2 = new CartItem("prod-1", 3, new Money(Currency.PLN, BigDecimal.valueOf(5)));

        cart.addItem(item1);

        assertThatThrownBy(() -> cart.addItem(item2))
            .isInstanceOf(InvalidCartItemException.class)
            .hasMessage("Cart item already added");
    }

    @Test
    void shouldReplaceCartItems() {
        Cart cart = Cart.newCart("user-1");
        CartItem oldItem = new CartItem("prod-1", 1, new Money(Currency.PLN, BigDecimal.valueOf(5)));
        CartItem newItem = new CartItem("prod-2", 2, new Money(Currency.PLN, BigDecimal.valueOf(10)));

        cart.addItem(oldItem);
        cart.replaceItems(Set.of(newItem));

        assertThat(cart.getItems()).containsExactly(newItem);
    }

    @Test
    void shouldCalculateTotalPricePrice() {
        Cart cart = Cart.newCart("user-1");
        cart.addItem(new CartItem("p1", 1, new Money(Currency.PLN, BigDecimal.valueOf(10))));
        cart.addItem(new CartItem("p2", 2, new Money(Currency.PLN, BigDecimal.valueOf(5)))); // 10

        Money total = cart.calculateTotalPrice();

        assertThat(total).isEqualTo(new Money(Currency.PLN, BigDecimal.valueOf(20)));
    }

    @Test
    void shouldReturnZeroWhenCartIsEmpty() {
        Cart cart = Cart.newCart("user-1");

        assertThat(cart.calculateTotalPrice()).isEqualTo(new Money(Currency.PLN, BigDecimal.ZERO));
    }

    @Test
    void shouldReturnUnmodifiableItemsSet() {
        Cart cart = Cart.newCart("user-1");

        assertThatThrownBy(() -> cart.getItems().add(
            new CartItem("p1", 1, new Money(Currency.PLN, BigDecimal.TEN))))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
