package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.valueobject.Money;
import java.time.LocalDateTime;
import java.util.UUID;

public interface GetBalanceUseCase {
    Money execute(UUID walletId, LocalDateTime at);
}
