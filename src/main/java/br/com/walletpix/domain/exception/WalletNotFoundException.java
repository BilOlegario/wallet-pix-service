package br.com.walletpix.domain.exception;

public class WalletNotFoundException extends ResourceNotFoundException {
    public WalletNotFoundException() {
        super("Carteira não encontrada");
    }

    public WalletNotFoundException(String message) {
        super(message);
    }
}
