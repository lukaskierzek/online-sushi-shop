package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

class InvalidCartException extends RuntimeException {
    InvalidCartException(String message) {
        super(message);
    }
}
