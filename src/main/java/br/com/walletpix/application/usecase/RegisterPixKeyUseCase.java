package br.com.walletpix.application.usecase;

import br.com.walletpix.domain.entity.PixKey;
import br.com.walletpix.domain.exception.WalletNotFoundException;
import br.com.walletpix.domain.repository.PixKeyRepository;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.valueobject.PixKeyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterPixKeyUseCase {

    private final PixKeyRepository pixKeyRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public PixKey execute(UUID walletId, PixKeyType type, String value) {
        walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);

        String keyValue = value;
        if (type == PixKeyType.EVP) {
            keyValue = UUID.randomUUID().toString();
        } else if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value is required for non-EVP keys");
        }

        if (pixKeyRepository.existsByValue(keyValue)) {
            throw new IllegalArgumentException("Pix key already registered");
        }

        PixKey pixKey = new PixKey(UUID.randomUUID(), walletId, type, keyValue);
        pixKeyRepository.save(pixKey);
        return pixKey;
    }
}
