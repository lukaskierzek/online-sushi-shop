package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

class InvalidCartException extends RuntimeException {
    InvalidCartException(String message) {
        super(message);
    }
}

class CartAlreadyExistsException extends RuntimeException {
    CartAlreadyExistsException(String message) {
        super(message);
    }
}

class CartAccessDeniedException extends RuntimeException {
    CartAccessDeniedException(String message) {
        super(message);
    }
}

class CartNotFoundException extends RuntimeException {
    CartNotFoundException(String message) {
        super(message);
    }
}
