package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {

    @Test
    void shouldCreateMoneyWithValidCurrencyAndAmount() {
        Currency usd = Currency.USD;
        BigDecimal amount = new BigDecimal("10.00");

        Money money = new Money(usd, amount);

        assertEquals(usd, money.currency());
        assertEquals(amount, money.amount());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        BigDecimal amount = new BigDecimal("5.00");

        Exception exception = assertThrows(InvalidMoneyException.class, () -> {
            new Money(null, amount);
        });

        assertEquals("Currency cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        Currency usd = Currency.USD;

        Exception exception = assertThrows(InvalidMoneyException.class, () -> {
            new Money(usd, null);
        });

        assertEquals("Amount must be non-null and >= 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        Currency usd = Currency.USD;
        BigDecimal negativeAmount = new BigDecimal("-1.00");

        Exception exception = assertThrows(InvalidMoneyException.class, () -> {
            new Money(usd, negativeAmount);
        });

        assertEquals("Amount must be non-null and >= 0", exception.getMessage());
    }

    @Test
    void shouldAddTwoMoneyObjectsWithSameCurrency() {
        Money m1 = new Money(Currency.EUR, new BigDecimal("20.50"));
        Money m2 = new Money(Currency.EUR, new BigDecimal("10.25"));

        Money result = m1.add(m2);

        assertEquals(Currency.EUR, result.currency());
        assertEquals(new BigDecimal("30.75"), result.amount());
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money m1 = new Money(Currency.USD, new BigDecimal("5.00"));
        Money m2 = new Money(Currency.EUR, new BigDecimal("5.00"));

        Exception exception = assertThrows(ProductPriceMismatchException.class, () -> {
            m1.add(m2);
        });

        assertEquals("Cannot add money with different currencies", exception.getMessage());
    }
}
