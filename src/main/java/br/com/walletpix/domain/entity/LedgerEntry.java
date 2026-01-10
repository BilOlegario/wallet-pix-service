package br.com.walletpix.domain.entity;

import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.LedgerEntryType;
import java.time.LocalDateTime;
import java.util.UUID;

public class LedgerEntry {
    private final UUID id;
    private final UUID walletId;
    private final Money amount;
    private final LedgerEntryType type;
    private final String description;
    private final LocalDateTime createdAt;

    public LedgerEntry(UUID id, UUID walletId, Money amount, LedgerEntryType type, String description,
            LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public Money getAmount() {
        return amount;
    }

    public LedgerEntryType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
