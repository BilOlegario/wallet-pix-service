package br.com.walletpix.infrastructure.persistence.repository;

import br.com.walletpix.domain.entity.PixTransfer;
import br.com.walletpix.domain.repository.PixTransferRepository;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.infrastructure.persistence.entity.PixTransferJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PixTransferRepositoryImpl implements PixTransferRepository {

    private final PixTransferJpaRepository jpaRepository;

    @Override
    public void save(PixTransfer transfer) {
        PixTransferJpaEntity entity = PixTransferJpaEntity.builder()
                .id(transfer.getId())
                .endToEndId(transfer.getEndToEndId())
                .senderWalletId(transfer.getSenderWalletId())
                .receiverKeyType(transfer.getReceiverKeyType())
                .receiverKeyValue(transfer.getReceiverKeyValue())
                .amount(transfer.getAmount().getAmount())
                .status(transfer.getStatus())
                .idempotencyKey(transfer.getIdempotencyKey())
                .createdAt(transfer.getCreatedAt())
                .build();
        jpaRepository.save(entity);
    }

    @Override
    public Optional<PixTransfer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PixTransfer> findByEndToEndId(String endToEndId) {
        return jpaRepository.findByEndToEndId(endToEndId).map(this::toDomain);
    }

    private PixTransfer toDomain(PixTransferJpaEntity entity) {
        return new PixTransfer(
                entity.getId(),
                entity.getEndToEndId(),
                entity.getSenderWalletId(),
                entity.getReceiverKeyType(),
                entity.getReceiverKeyValue(),
                new Money(entity.getAmount()),
                entity.getStatus(),
                entity.getIdempotencyKey(),
                entity.getCreatedAt());
    }
}
