package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.infrastructure.persistence.entity.PixTransferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PixTransferJpaRepository extends JpaRepository<PixTransferJpaEntity, UUID> {
    Optional<PixTransferJpaEntity> findByEndToEndId(String endToEndId);
}
