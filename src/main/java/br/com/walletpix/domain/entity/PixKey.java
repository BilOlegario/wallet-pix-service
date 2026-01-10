package br.com.walletpix.domain.entity;

import br.com.walletpix.domain.valueobject.PixKeyType;
import java.util.UUID;

/**
 * Entidade de Domínio PixKey.
 */
public class PixKey {
    private final UUID id;
    private final UUID walletId;
    private final PixKeyType type;
    private final String value;

    public PixKey(UUID id, UUID walletId, PixKeyType type, String value) {
        this.id = id;
        this.walletId = walletId;
        this.type = type;
        this.value = value;
        validate();
    }

    private void validate() {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Pix key value cannot be empty");
        }
        // Adicionar validações específicas por tipo no futuro se necessário
    }

    public UUID getId() {
        return id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public PixKeyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
