package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.PixTransfer;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.PixKeyType;
import java.util.UUID;

public interface SendPixUseCase {
    PixTransfer execute(UUID senderWalletId, PixKeyType receiverKeyType,
            String receiverKeyValue, Money amount, String idempotencyKey);
}
