package br.com.walletpix.domain.valueobject;

import br.com.walletpix.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Deve somar valores corretamente")
    void shouldAddValues() {
        Money m1 = new Money(new BigDecimal("10.00"));
        Money m2 = new Money(new BigDecimal("5.50"));

        Money result = m1.add(m2);

        assertEquals(new BigDecimal("15.50"), result.getAmount());
    }

    @Test
    @DisplayName("Deve subtrair valores corretamente")
    void shouldSubtractValues() {
        Money m1 = new Money(new BigDecimal("10.00"));
        Money m2 = new Money(new BigDecimal("4.00"));

        Money result = m1.subtract(m2);

        assertEquals(new BigDecimal("6.00"), result.getAmount());
    }

    @Test
    @DisplayName("Deve lançar exceção ao subtrair valor maior que o saldo")
    void shouldThrowExceptionWhenSubtractionResultsInNegative() {
        Money m1 = new Money(new BigDecimal("10.00"));
        Money m2 = new Money(new BigDecimal("10.01"));

        assertThrows(InsufficientBalanceException.class, () -> m1.subtract(m2));
    }

    @Test
    @DisplayName("Deve garantir escala de 2 casas decimais")
    void shouldMaintainTwoDecimalScale() {
        Money m = new Money(new BigDecimal("10.123"));
        assertEquals(new BigDecimal("10.12"), m.getAmount());
    }
}
