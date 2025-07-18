package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import java.math.BigDecimal;

record Money(Currency currency, BigDecimal amount) {
}
