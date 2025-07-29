package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

abstract class CartDomainException extends RuntimeException {
    protected CartDomainException(String message) {
        super(message);
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

class InvalidOwnerIdException extends CartDomainException {

    InvalidOwnerIdException(String message) {
        super(message);
    }
}

class CartNotFoundException extends RuntimeException {

    CartNotFoundException(String message) {
        super(message);
    }
}

class CartItemPriceProcessingException extends RuntimeException {
    CartItemPriceProcessingException(String message) {
        super(message);
    }
}
