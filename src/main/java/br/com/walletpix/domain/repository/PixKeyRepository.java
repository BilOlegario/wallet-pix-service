package br.com.walletpix.domain.repository;

import br.com.walletpix.domain.entity.PixKey;
import java.util.Optional;
import java.util.UUID;

public interface PixKeyRepository {
    PixKey save(PixKey pixKey);

    Optional<PixKey> findByValue(String value);

    boolean existsByValue(String value);
}
