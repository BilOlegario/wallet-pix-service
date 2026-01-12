package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.exception.InvalidHistoricalDateException;
import br.com.walletpix.domain.exception.ResourceNotFoundException;
import br.com.walletpix.domain.repository.LedgerRepository;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Override
    public Money execute(UUID walletId, LocalDateTime at) {
        if (at != null) {
            if (at.isAfter(LocalDateTime.now())) {
                throw new InvalidHistoricalDateException();
            }

            walletRepository.findById(walletId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

            return ledgerRepository.getBalanceAt(walletId, at);
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        return wallet.getBalance();
    }
}
