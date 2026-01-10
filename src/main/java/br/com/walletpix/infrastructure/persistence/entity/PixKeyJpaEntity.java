package br.com.walletpix.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "pix_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyJpaEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "key_type", nullable = false)
    private String keyType;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;
}
