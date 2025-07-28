package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {

    @Test
    void shouldCreateValidMoney() {
        Money money = new Money(Currency.PLN, new BigDecimal("100.00"));
        assertEquals(Currency.PLN, money.currency());
    }

    @Test
    void shouldThrowForNullCurrency() {
        assertThrows(InvalidMoneyException.class, () -> new Money(null, BigDecimal.TEN));
    }

    @Test
    void shouldThrowForNegativeAmount() {
        assertThrows(InvalidMoneyException.class, () -> new Money(Currency.PLN, new BigDecimal("-1.00")));
    }

    @Test
    void shouldAddMoneyOfSameCurrency() {
        Money m1 = new Money(Currency.PLN, new BigDecimal("5.00"));
        Money m2 = new Money(Currency.PLN, new BigDecimal("7.50"));

        Money result = m1.add(m2);
        assertEquals(new Money(Currency.PLN, new BigDecimal("12.50")), result);
    }

    @Test
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money m1 = new Money(Currency.PLN, BigDecimal.TEN);
        Money m2 = new Money(Currency.EUR, BigDecimal.TEN);

        assertThrows(ProductPriceMismatchException.class, () -> m1.add(m2));
    }
}
