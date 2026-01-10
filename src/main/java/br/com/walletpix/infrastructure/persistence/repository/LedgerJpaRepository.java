package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.infrastructure.persistence.entity.LedgerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LedgerJpaRepository extends JpaRepository<LedgerJpaEntity, UUID> {
}
