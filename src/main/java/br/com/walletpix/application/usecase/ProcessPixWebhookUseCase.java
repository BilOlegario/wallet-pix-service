package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.valueobject.PixStatus;

public interface ProcessPixWebhookUseCase {
    void execute(String eventId, String endToEndId, PixStatus status);
}
