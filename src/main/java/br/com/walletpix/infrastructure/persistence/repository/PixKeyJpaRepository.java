package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.infrastructure.persistence.entity.PixKeyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PixKeyJpaRepository extends JpaRepository<PixKeyJpaEntity, UUID> {
    Optional<PixKeyJpaEntity> findByKeyValue(String keyValue);

    boolean existsByKeyValue(String keyValue);
}
