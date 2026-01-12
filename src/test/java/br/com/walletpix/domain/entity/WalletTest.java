package br.com.walletpix.domain.entity;

import br.com.walletpix.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    @DisplayName("Deve realizar depósito corretamente")
    void shouldDepositSuccessfully() {
        Wallet wallet = new Wallet(UUID.randomUUID(), Money.ZERO, 0L);
        Money amount = new Money(new BigDecimal("100.00"));

        wallet.deposit(amount);

        assertEquals(amount, wallet.getBalance());
    }

    @Test
    @DisplayName("Deve realizar saque corretamente")
    void shouldWithdrawSuccessfully() {
        Wallet wallet = new Wallet(UUID.randomUUID(), new Money(new BigDecimal("100.00")), 0L);
        Money amount = new Money(new BigDecimal("40.00"));

        wallet.withdraw(amount);

        assertEquals(new Money(new BigDecimal("60.00")), wallet.getBalance());
    }
}
