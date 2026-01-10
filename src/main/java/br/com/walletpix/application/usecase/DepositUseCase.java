package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.entity.LedgerEntry;
import br.com.walletpix.domain.exception.WalletNotFoundException;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.repository.LedgerRepository;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.domain.valueobject.LedgerEntryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositUseCase {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void execute(UUID walletId, Money amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);

        wallet.deposit(amount);
        walletRepository.save(wallet);

        LedgerEntry entry = new LedgerEntry(
                UUID.randomUUID(),
                walletId,
                amount,
                LedgerEntryType.CREDIT,
                "Regular deposit",
                LocalDateTime.now());
        ledgerRepository.save(entry);
    }
}
