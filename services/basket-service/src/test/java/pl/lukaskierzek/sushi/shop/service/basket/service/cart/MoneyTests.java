package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class MoneyTests {

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateMoneyWithSupportedCurrencies(Currency currency) {
        Money money = new Money(currency, BigDecimal.TEN);

        assertThat(money.currency()).isEqualTo(currency);
        assertThat(money.amount()).isEqualByComparingTo("10");
    }

    @Test
    void shouldAddTwoMoneyWithSameCurrency() {
        Money m1 = new Money(Currency.PLN, BigDecimal.valueOf(5));
        Money m2 = new Money(Currency.PLN, BigDecimal.valueOf(15));

        Money result = m1.add(m2);

        assertThat(result).isEqualTo(new Money(Currency.PLN, BigDecimal.valueOf(20)));
    }

    @ParameterizedTest
    @MethodSource("provideDifferentCurrencyPairs")
    void shouldNotAddMoneyWithDifferentCurrencies(Currency c1, Currency c2) {
        Money m1 = new Money(c1, BigDecimal.ONE);
        Money m2 = new Money(c2, BigDecimal.TEN);

        assertThatThrownBy(() -> m1.add(m2))
            .isInstanceOf(ProductPriceMismatchException.class)
            .hasMessage("Cannot add money with different currencies");
    }

    private static Stream<Arguments> provideDifferentCurrencyPairs() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(Currency.PLN, Currency.EUR),
            org.junit.jupiter.params.provider.Arguments.of(Currency.EUR, Currency.PLN),
            org.junit.jupiter.params.provider.Arguments.of(Currency.PLN, Currency.USD),
            org.junit.jupiter.params.provider.Arguments.of(Currency.USD, Currency.EUR)
        );
    }

    @Test
    void shouldNotAllowNullCurrency() {
        assertThatThrownBy(() -> new Money(null, BigDecimal.ONE))
            .isInstanceOf(InvalidMoneyException.class)
            .hasMessage("Currency cannot be null");
    }

    @Test
    void shouldNotAllowNullOrNegativeAmount() {
        assertThatThrownBy(() -> new Money(Currency.PLN, null))
            .isInstanceOf(InvalidMoneyException.class)
            .hasMessage("Amount must be non-null and >= 0");

        assertThatThrownBy(() -> new Money(Currency.PLN, BigDecimal.valueOf(-1)))
            .isInstanceOf(InvalidMoneyException.class);
    }
}
