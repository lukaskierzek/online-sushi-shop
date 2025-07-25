package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
class CartController {

    private final CartService service;

    @PostMapping
        //TODO: add retrieving userId from JWT
    ResponseEntity<String> post(String userId) {
        var cartId = service.createCart(userId);
        return created(create("/carts/" + cartId)).body(cartId);
    }

    @GetMapping
    CartResponse get(String userId) {
        return service.getCart(userId);
    }

    @PutMapping
    void put(String userId, @RequestBody CartItemsRequest request) {
        service.updateCart(userId, request);
    }

    record CartResponse(Set<CartItemResponse> items, Money totalPrice) {
    }

    record CartItemResponse(String id, Integer quantity, Money price) {
    }

    record CartItemsRequest(Set<CartItemRequest> items) {
    }

    record CartItemRequest(String id, Integer quantity) {
    }
}
