package br.com.walletpix.domain.repository;

import br.com.walletpix.domain.entity.LedgerEntry;

public interface LedgerRepository {
    void save(LedgerEntry entry);
}
