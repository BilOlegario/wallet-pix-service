package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.LedgerEntry;
import br.com.walletpix.domain.entity.PixTransfer;
import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.exception.ResourceNotFoundException;
import br.com.walletpix.domain.repository.IdempotencyRepository;
import br.com.walletpix.domain.repository.LedgerRepository;
import br.com.walletpix.domain.repository.PixTransferRepository;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.valueobject.LedgerEntryType;
import br.com.walletpix.domain.valueobject.PixStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPixWebhookUseCaseImpl implements ProcessPixWebhookUseCase {

    private final PixTransferRepository pixTransferRepository;
    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;
    private final IdempotencyRepository idempotencyRepository;

    private static final String SCOPE = "webhook-event";

    @Override
    @Transactional
    public void execute(String eventId, String endToEndId, PixStatus newStatus) {
        log.info("Recebido webhook Pix. eventId={}, endToEndId={}, status={}", eventId, endToEndId, newStatus);

        if (!idempotencyRepository.isNotProcessed(SCOPE, eventId)) {
            log.info("Evento de webhook já processado. eventId={}", eventId);
            return;
        }
        PixTransfer transfer = pixTransferRepository.findByEndToEndId(endToEndId)
                .orElseThrow(() -> new ResourceNotFoundException("Transferência Pix não encontrada"));

        if (transfer.getStatus() != PixStatus.PENDING) {
            return;
        }

        if (newStatus == PixStatus.CONFIRMED) {
            transfer.confirm();
        } else if (newStatus == PixStatus.REJECTED) {
            transfer.reject();

            Wallet wallet = walletRepository.findById(transfer.getSenderWalletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para estorno"));

            wallet.deposit(transfer.getAmount());
            walletRepository.save(wallet);

            LedgerEntry ledgerEntry = new LedgerEntry(
                    UUID.randomUUID(),
                    wallet.getId(),
                    transfer.getAmount(),
                    LedgerEntryType.CREDIT,
                    "Estorno Pix (Rejeitado): " + endToEndId,
                    LocalDateTime.now());
            ledgerRepository.save(ledgerEntry);
        }

        pixTransferRepository.save(transfer);

        idempotencyRepository.markAsProcessed(SCOPE, eventId);
        log.info("Processamento de webhook finalizado. endToEndId={}, status={}", endToEndId, newStatus);
    }
}
