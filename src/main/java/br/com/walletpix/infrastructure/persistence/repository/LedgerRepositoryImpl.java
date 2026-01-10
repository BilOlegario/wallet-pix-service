package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.domain.entity.LedgerEntry;
import br.com.walletpix.domain.repository.LedgerRepository;
import br.com.walletpix.infrastructure.persistence.entity.LedgerJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LedgerRepositoryImpl implements LedgerRepository {

    private final LedgerJpaRepository jpaRepository;

    @Override
    public void save(LedgerEntry entry) {
        LedgerJpaEntity jpaEntity = LedgerJpaEntity.builder()
                .id(entry.getId())
                .walletId(entry.getWalletId())
                .amount(entry.getAmount().getAmount())
                .type(entry.getType())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .build();
        jpaRepository.save(jpaEntity);
    }
}
