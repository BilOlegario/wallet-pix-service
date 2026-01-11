package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.infrastructure.persistence.entity.LedgerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface LedgerJpaRepository extends JpaRepository<LedgerJpaEntity, UUID> {
    @Query("SELECT SUM(CASE WHEN l.type = 'CREDIT' THEN l.amount ELSE -l.amount END) " +
            "FROM LedgerJpaEntity l WHERE l.walletId = :walletId AND l.createdAt <= :at")
    BigDecimal sumAmountByWalletIdAndCreatedAtBefore(UUID walletId, LocalDateTime at);
}
