package br.com.walletpix.infrastructure.persistence.entity;

import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.domain.valueobject.PixStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pix_transfers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixTransferJpaEntity {

    @Id
    private UUID id;

    @Column(name = "end_to_end_id", unique = true, nullable = false)
    private String endToEndId;

    @Column(name = "sender_wallet_id", nullable = false)
    private UUID senderWalletId;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_key_type", nullable = false)
    private PixKeyType receiverKeyType;

    @Column(name = "receiver_key_value", nullable = false)
    private String receiverKeyValue;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixStatus status;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
