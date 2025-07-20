package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

class InvalidCategoryException extends RuntimeException {
    InvalidCategoryException(String message) {
        super(message);
    }
}

class CategoryNotFoundException extends RuntimeException {
    CategoryNotFoundException(String message) {
        super(message);
    }
}
