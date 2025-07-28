package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
class CartController {

    private static final String ANONYMOUS_ID_HEADER = "X-Anonymous-Id";

    private final CartService service;

    @PostMapping
    ResponseEntity<String> post(@AuthenticationPrincipal Jwt jwt,
                                @RequestHeader(value = ANONYMOUS_ID_HEADER, required = false) String anonymousId) {
        var cartId = service.createCart(resolveOwnerId(jwt, anonymousId));
        return created(create("/carts/" + cartId)).body(cartId);
    }

    @GetMapping
    CartResponse get(@AuthenticationPrincipal Jwt jwt,
                     @RequestHeader(value = ANONYMOUS_ID_HEADER, required = false) String anonymousId) {
        return service.getCart(resolveOwnerId(jwt, anonymousId));
    }

    @PutMapping
    void put(@AuthenticationPrincipal Jwt jwt,
             @RequestHeader(value = ANONYMOUS_ID_HEADER, required = false) String anonymousId,
             @RequestBody CartItemsRequest request) {
        service.updateCart(resolveOwnerId(jwt, anonymousId), request);
    }

    private OwnerId resolveOwnerId(Jwt jwt, String anonymousId) {
        if (jwt != null) {
            return new OwnerId(jwt.getSubject(), null);
        }

        if (anonymousId != null) {
            return new OwnerId(null, anonymousId);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing user identifier");
    }
}

record CartResponse(Set<CartItemResponse> items, Money totalPrice) {
}

record CartItemResponse(String id, Integer quantity, Money price) {
}

record CartItemsRequest(Set<CartItemRequest> items) {
}

record CartItemRequest(String id, Integer quantity) {
}

@RestControllerAdvice
class CartExceptionHandler {

    @ExceptionHandler(CartDomainException.class)
    ResponseEntity<Map<String, String>> handleCartDomainExceptions(CartDomainException ex) {
        var body = Map.of("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CartNotFoundException.class)
    ResponseEntity<Map<String, String>> handleCartNotFound(CartNotFoundException ex) {
        var body = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
