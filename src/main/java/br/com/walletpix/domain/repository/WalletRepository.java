package br.com.walletpix.domain.repository;

import br.com.walletpix.domain.entity.Wallet;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID id);
}
