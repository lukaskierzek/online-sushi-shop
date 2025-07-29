package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OwnerIdTests {

    @Test
    void shouldCreateValidOwnerId() {
        OwnerId id = new OwnerId("user123", "anon456");
        assertEquals("user123", id.userId());
    }

    @Test
    void shouldThrowForEmptyUserId() {
        assertThrows(InvalidOwnerIdException.class, () -> new OwnerId("", ""));
    }
}
