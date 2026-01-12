package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.entity.LedgerEntry;
import br.com.walletpix.domain.exception.WalletNotFoundException;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.repository.LedgerRepository;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.LedgerEntryType;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawUseCase {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 10, backoff = @Backoff(delay = 100, multiplier = 2, random = true))
    public void execute(UUID walletId, Money amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);

        wallet.withdraw(amount);
        walletRepository.save(wallet);

        LedgerEntry entry = new LedgerEntry(
                UUID.randomUUID(),
                walletId,
                amount,
                LedgerEntryType.DEBIT,
                "Regular withdrawal",
                LocalDateTime.now());
        ledgerRepository.save(entry);
    }
}
