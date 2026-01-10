package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateWalletUseCase {

    private final WalletRepository walletRepository;

    @Transactional
    public UUID execute() {
        Wallet wallet = Wallet.createNew();
        return walletRepository.save(wallet).getId();
    }
}
