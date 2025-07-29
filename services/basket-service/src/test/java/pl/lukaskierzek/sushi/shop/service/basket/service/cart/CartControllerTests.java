package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;
import pl.lukaskierzek.sushi.shop.service.basket.service.IntegrationTest;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.*;

class CartControllerTests extends IntegrationTest {

    static final String ANONYMOUS_ID = UUID.randomUUID().toString();
    static final String USER_ID = UUID.randomUUID().toString();

    @ParameterizedTest
    @MethodSource("productCartCreationDtos")
    void shouldCreateCartAuthenticated(Pair<MockHttpServletRequestBuilder, OwnerId> args) throws Exception {
        mvc.perform(args.getKey())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().string(notNullValue()));

        var cart = redisTemplate.opsForValue().get("carts::" + args.getValue());
        assertNotNull(cart);
    }

    @ParameterizedTest
    @MethodSource("produceUnauthorizedMessages")
    void shouldReturnUnauthorizedWhenNoUserIdentification(MockHttpServletRequestBuilder builder) throws Exception {
        mvc.perform(builder.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Missing user identifier"));
    }

    static Stream<Pair<MockHttpServletRequestBuilder, OwnerId>> productCartCreationDtos() {
        return Stream.of(
            Pair.of(post("/carts")
                    .with(jwt().jwt(jwt -> jwt.subject(USER_ID))),
                new OwnerId(USER_ID, null)),
            Pair.of(post("/carts")
                    .header("X-Anonymous-Id", ANONYMOUS_ID),
                new OwnerId(null, ANONYMOUS_ID)));
    }

    static Stream<MockHttpServletRequestBuilder> produceUnauthorizedMessages() {
        return Stream.of(
            post("/carts"),
            put("/carts").content("{ \"items\": [] }"),
            get("/carts"));
    }
}
