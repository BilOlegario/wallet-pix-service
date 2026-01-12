package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.infrastructure.persistence.entity.IdempotencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyJpaEntity, Long> {
    Optional<IdempotencyJpaEntity> findByScopeAndIdempotencyKey(String scope, String idempotencyKey);
}
