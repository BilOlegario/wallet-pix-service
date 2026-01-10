package br.com.walletpix.domain.exception;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException() {
        super("Saldo insuficiente para realizar a operação");
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
