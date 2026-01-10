package br.com.walletpix.infrastructure.persistence.adapter;

import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.infrastructure.persistence.entity.WalletJpaEntity;
import br.com.walletpix.infrastructure.persistence.repository.WalletJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepository {

    private final WalletJpaRepository walletJpaRepository;

    @Override
    public Wallet save(Wallet wallet) {
        WalletJpaEntity entity = WalletJpaEntity.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance().getAmount())
                .version(wallet.getVersion())
                .build();

        WalletJpaEntity saved = walletJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return walletJpaRepository.findById(id).map(this::toDomain);
    }

    private Wallet toDomain(WalletJpaEntity entity) {
        return new Wallet(entity.getId(), new Money(entity.getBalance()), entity.getVersion());
    }
}
