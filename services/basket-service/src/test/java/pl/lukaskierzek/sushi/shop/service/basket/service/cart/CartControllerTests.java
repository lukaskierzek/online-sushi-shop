package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;
import pl.lukaskierzek.sushi.shop.service.basket.service.IntegrationTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.*;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartFixture.UPDATE_CART_REQUEST;

class CartControllerTests extends IntegrationTest {

    static final String ANONYMOUS_ID = UUID.randomUUID().toString();
    static final String USER_ID = UUID.randomUUID().toString();

    @ParameterizedTest
    @MethodSource("produceCreateCartParams")
    void shouldCreateEmptyCart(Pair<MockHttpServletRequestBuilder, OwnerId> args) throws Exception {
        mvc.perform(args.getKey())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().string(notNullValue()));

        var cart = redisTemplate.opsForValue().get("carts::" + args.getValue());
        assertNotNull(cart);
    }

    @ParameterizedTest
    @MethodSource("produceUpdateCartParams")
    void shouldUpdateCart(Pair<MockHttpServletRequestBuilder, OwnerId> args) throws Exception {
        var cart = Cart.newCart(args.getValue());
        redisTemplate.opsForValue().set("carts::" + args.getValue(), cart);

        mvc.perform(args.getKey()
                .content(UPDATE_CART_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(StringUtils.EMPTY));

        var updatedCart = (Cart) redisTemplate.opsForValue().get("carts::" + args.getValue());
        assertNotNull(cart);
        assertNotNull(updatedCart);
        assertEquals(cart.getId(), updatedCart.getId());
        assertThat(updatedCart.getItems()).hasSize(4);
    }

    @ParameterizedTest
    @MethodSource("produceGetCartParams")
    void shouldGetCart(Pair<MockHttpServletRequestBuilder, OwnerId> args) throws Exception {
        var cart = Cart.newCart(args.getValue());
        cart.addCartItem(UUID.randomUUID().toString(), 1, new Money(Currency.PLN, new BigDecimal("200")));
        cart.addCartItem(UUID.randomUUID().toString(), 2, new Money(Currency.PLN, new BigDecimal("201")));
        cart.addCartItem(UUID.randomUUID().toString(), 3, new Money(Currency.PLN, new BigDecimal("202")));
        cart.addCartItem(UUID.randomUUID().toString(), 4, new Money(Currency.PLN, new BigDecimal("203")));
        cart.addCartItem(UUID.randomUUID().toString(), 5, new Money(Currency.PLN, new BigDecimal("204")));

        redisTemplate.opsForValue().set("carts::" + args.getValue(), cart);
        cart.getItems().forEach(cartItem -> redisTemplate.opsForSet()
            .add("product-to-owners::" + cartItem.productId(), args.getValue()));

        var items = cart.getItems().stream()
            .map(item -> new CartItemResponse(item.productId(), item.quantity(), item.unitPrice()))
            .collect(Collectors.toSet());

        var totalPrice = cart.calculateTotalPrice();
        var cartResponse = new CartResponse(items, totalPrice);

        var expectedJson = objectMapper.writeValueAsString(cartResponse);

        mvc.perform(args.getKey().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @ParameterizedTest
    @MethodSource("produceUnauthorizedMessages")
    void shouldReturnUnauthorizedWhenNoUserIdentification(MockHttpServletRequestBuilder builder) throws Exception {
        mvc.perform(builder.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Missing user identifier"));
    }

    static Stream<Pair<MockHttpServletRequestBuilder, OwnerId>> produceCreateCartParams() {
        return Stream.of(
            Pair.of(post("/carts")
                    .with(jwt().jwt(jwt -> jwt.subject(USER_ID))),
                new OwnerId(USER_ID, null)),
            Pair.of(post("/carts")
                    .header("X-Anonymous-Id", ANONYMOUS_ID),
                new OwnerId(null, ANONYMOUS_ID)));
    }

    public static Stream<Pair<MockHttpServletRequestBuilder, OwnerId>> produceUpdateCartParams() {
        return Stream.of(
            Pair.of(put("/carts")
                    .with(jwt().jwt(jwt -> jwt.subject(USER_ID))),
                new OwnerId(USER_ID, null)),
            Pair.of(put("/carts")
                    .header("X-Anonymous-Id", ANONYMOUS_ID),
                new OwnerId(null, ANONYMOUS_ID)));
    }

    static Stream<Pair<MockHttpServletRequestBuilder, OwnerId>> produceGetCartParams() {
        return Stream.of(
            Pair.of(get("/carts")
                    .with(jwt().jwt(jwt -> jwt.subject(USER_ID))),
                new OwnerId(USER_ID, null)),
            Pair.of(get("/carts")
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
