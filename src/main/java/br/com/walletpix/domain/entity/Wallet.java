package br.com.walletpix.domain.entity;

import br.com.walletpix.domain.valueobject.Money;
import java.util.UUID;

/**
 * Entidade de Domínio Wallet.
 * Focada em regras de negócio puras.
 */
public class Wallet {
    private final UUID id;
    private Money balance;
    private final Long version;

    public Wallet(UUID id, Money balance, Long version) {
        this.id = id;
        this.balance = balance;
        this.version = version;
    }

    public static Wallet createNew() {
        return new Wallet(UUID.randomUUID(), Money.ZERO, 0L);
    }

    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
    }

    public UUID getId() {
        return id;
    }

    public Money getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }
}
