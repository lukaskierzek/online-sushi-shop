package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
class InvalidProductException extends RuntimeException {
    InvalidProductException(String message) {
        super(message);
    }
}
