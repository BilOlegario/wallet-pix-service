package br.com.walletpix.domain.repository;

import br.com.walletpix.domain.entity.PixTransfer;
import java.util.Optional;
import java.util.UUID;

public interface PixTransferRepository {
    void save(PixTransfer transfer);

    Optional<PixTransfer> findById(UUID id);

    Optional<PixTransfer> findByEndToEndId(String endToEndId);
}
