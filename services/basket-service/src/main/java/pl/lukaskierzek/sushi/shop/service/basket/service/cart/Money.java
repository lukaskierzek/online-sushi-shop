package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import java.io.Serializable;
import java.math.BigDecimal;

record Money(Currency currency, BigDecimal amount) implements Serializable {

    Money {
        if (currency == null) {
            throw new InvalidMoneyException("Currency cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("Amount must be non-null and >= 0");
        }
    }

    Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new ProductPriceMismatchException("Cannot add money with different currencies");
        }
        return new Money(currency, amount.add(other.amount));
    }
}
