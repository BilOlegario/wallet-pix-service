package br.com.walletpix.infrastructure.persistence.adapter;

import br.com.walletpix.domain.entity.PixKey;
import br.com.walletpix.domain.repository.PixKeyRepository;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.infrastructure.persistence.entity.PixKeyJpaEntity;
import br.com.walletpix.infrastructure.persistence.repository.PixKeyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PixKeyRepositoryAdapter implements PixKeyRepository {

    private final PixKeyJpaRepository pixKeyJpaRepository;

    @Override
    public PixKey save(PixKey pixKey) {
        PixKeyJpaEntity entity = PixKeyJpaEntity.builder()
                .id(pixKey.getId())
                .walletId(pixKey.getWalletId())
                .keyType(pixKey.getType().name())
                .keyValue(pixKey.getValue())
                .build();

        PixKeyJpaEntity saved = pixKeyJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PixKey> findByValue(String value) {
        return pixKeyJpaRepository.findByKeyValue(value).map(this::toDomain);
    }

    @Override
    public boolean existsByValue(String value) {
        return pixKeyJpaRepository.existsByKeyValue(value);
    }

    private PixKey toDomain(PixKeyJpaEntity entity) {
        return new PixKey(
                entity.getId(),
                entity.getWalletId(),
                PixKeyType.valueOf(entity.getKeyType()),
                entity.getKeyValue());
    }
}
