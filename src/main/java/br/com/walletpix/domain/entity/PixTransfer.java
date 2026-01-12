package br.com.walletpix.domain.entity;

import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.domain.valueobject.PixStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class PixTransfer {
    private final UUID id;
    private final String endToEndId;
    private final UUID senderWalletId;
    private final PixKeyType receiverKeyType;
    private final String receiverKeyValue;
    private final Money amount;
    private PixStatus status;
    private final String idempotencyKey;
    private final LocalDateTime createdAt;

    public PixTransfer(UUID id, String endToEndId, UUID senderWalletId,
            PixKeyType receiverKeyType, String receiverKeyValue,
            Money amount, PixStatus status, String idempotencyKey,
            LocalDateTime createdAt) {
        this.id = id;
        this.endToEndId = endToEndId;
        this.senderWalletId = senderWalletId;
        this.receiverKeyType = receiverKeyType;
        this.receiverKeyValue = receiverKeyValue;
        this.amount = amount;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public void confirm() {
        if (this.status != PixStatus.PENDING) {
            throw new IllegalStateException("Apenas transferências PENDING podem ser confirmadas");
        }
        this.status = PixStatus.CONFIRMED;
    }

    public void reject() {
        if (this.status != PixStatus.PENDING) {
            throw new IllegalStateException("Apenas transferências PENDING podem ser rejeitadas");
        }
        this.status = PixStatus.REJECTED;
    }

    public UUID getId() {
        return id;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public UUID getSenderWalletId() {
        return senderWalletId;
    }

    public PixKeyType getReceiverKeyType() {
        return receiverKeyType;
    }

    public String getReceiverKeyValue() {
        return receiverKeyValue;
    }

    public Money getAmount() {
        return amount;
    }

    public PixStatus getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
