package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import java.math.BigDecimal;

record Money(Currency currency, BigDecimal amount) {

    Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");//TODO: custom exceptions
        }
    }

    Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(currency, amount.add(other.amount));
    }
}
