package br.com.walletpix.domain.repository;

import br.com.walletpix.domain.entity.LedgerEntry;
import br.com.walletpix.domain.valueobject.Money;
import java.time.LocalDateTime;
import java.util.UUID;

public interface LedgerRepository {
    void save(LedgerEntry entry);

    Money getBalanceAt(UUID walletId, LocalDateTime at);
}
