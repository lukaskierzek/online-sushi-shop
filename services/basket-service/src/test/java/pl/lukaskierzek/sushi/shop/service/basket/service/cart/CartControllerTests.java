package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;
import pl.lukaskierzek.sushi.shop.service.basket.service.IntegrationTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.*;

class CartControllerTests extends IntegrationTest {

    @Test
    void shouldCreateCartAuthenticated() throws Exception {
        mvc.perform(post("/carts")
                        .with(jwt().jwt(jwt -> jwt.subject("Alfred"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().string(notNullValue()));

        var cart = redisTemplate.opsForValue().get("carts::" + new OwnerId("Alfred", null));
        assertNotNull(cart);
    }

    @Test
    void shouldCreateCartForAnonymousUser() throws Exception {
        var anonymousId = UUID.randomUUID().toString();

        mvc.perform(post("/carts")
                        .header("X-Anonymous-Id", anonymousId))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().string(notNullValue()));

        var cart = redisTemplate.opsForValue().get("carts::" + new OwnerId(null, anonymousId));
        assertNotNull(cart);
    }

    @Test
    void shouldReturnUnauthorizedWhenNoUserIdentification() throws Exception {
        mvc.perform(post("/carts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing user identifier"));
    }
}
