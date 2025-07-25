package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

abstract class CartDomainException extends RuntimeException {
    protected CartDomainException(String message) {
        super(message);
    }

    protected CartDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

class InvalidCartException extends CartDomainException {
    InvalidCartException(String message) {
        super(message);
    }
}

class InvalidCartItemException extends CartDomainException {
    InvalidCartItemException(String message) {
        super(message);
    }
}

class CartAlreadyExistsException extends CartDomainException {
    CartAlreadyExistsException(String message) {
        super(message);
    }
}

class CartNotFoundException extends CartDomainException {
    CartNotFoundException(String message) {
        super(message);
    }
}

class ProductPriceMismatchException extends CartDomainException {
    ProductPriceMismatchException(String message) {
        super(message);
    }
}

class InvalidMoneyException extends CartDomainException {
    InvalidMoneyException(String message) {
        super(message);
    }
}
