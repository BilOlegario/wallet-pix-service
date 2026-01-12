package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.domain.repository.IdempotencyRepository;
import br.com.walletpix.infrastructure.persistence.entity.IdempotencyJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class IdempotencyRepositoryImpl implements IdempotencyRepository {

    private final IdempotencyJpaRepository jpaRepository;

    @Override
    public boolean isNotProcessed(String scope, String key) {
        return jpaRepository.findByScopeAndIdempotencyKey(scope, key).isEmpty();
    }

    @Override
    public void markAsProcessed(String scope, String key) {
        IdempotencyJpaEntity entity = IdempotencyJpaEntity.builder()
                .scope(scope)
                .idempotencyKey(key)
                .createdAt(LocalDateTime.now())
                .build();
        jpaRepository.save(entity);
    }
}
