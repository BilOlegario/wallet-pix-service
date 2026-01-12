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
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.domain.valueobject.PixStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendPixUseCaseImpl implements SendPixUseCase {

        private final WalletRepository walletRepository;
        private final LedgerRepository ledgerRepository;
        private final PixTransferRepository pixTransferRepository;
        private final IdempotencyRepository idempotencyRepository;

        private static final String SCOPE = "pix-transfer";

        @Override
        @Transactional
        @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 10, backoff = @Backoff(delay = 100, multiplier = 2, random = true))
        public PixTransfer execute(UUID senderWalletId, PixKeyType receiverKeyType,
                        String receiverKeyValue, Money amount, String idempotencyKey) {

                if (!idempotencyRepository.isNotProcessed(SCOPE, idempotencyKey)) {
                        log.warn("Tentativa de Pix duplicado detectada. scope={}, key={}", SCOPE, idempotencyKey);
                        throw new IllegalStateException("Operação já processada para esta chave de idempotência");
                }

                Wallet senderWallet = walletRepository.findById(senderWalletId)
                                .orElseThrow(() -> new ResourceNotFoundException("Carteira de origem não encontrada"));

                senderWallet.withdraw(amount);
                walletRepository.save(senderWallet);

                String endToEndId = "E" + UUID.randomUUID().toString().replace("-", "").substring(0, 31);
                log.info("Iniciando transferência Pix. endToEndId={}, idempotencyKey={}, amount={}",
                                endToEndId, idempotencyKey, amount.getAmount());

                LedgerEntry ledgerEntry = new LedgerEntry(
                                UUID.randomUUID(),
                                senderWalletId,
                                amount,
                                LedgerEntryType.DEBIT,
                                "Envio Pix: " + endToEndId,
                                LocalDateTime.now());
                ledgerRepository.save(ledgerEntry);

                PixTransfer transfer = new PixTransfer(
                                UUID.randomUUID(),
                                endToEndId,
                                senderWalletId,
                                receiverKeyType,
                                receiverKeyValue,
                                amount,
                                PixStatus.PENDING,
                                idempotencyKey,
                                LocalDateTime.now());
                pixTransferRepository.save(transfer);

                idempotencyRepository.markAsProcessed(SCOPE, idempotencyKey);

                log.info("Transferência Pix criada com sucesso. endToEndId={}", endToEndId);

                return transfer;
        }
}
