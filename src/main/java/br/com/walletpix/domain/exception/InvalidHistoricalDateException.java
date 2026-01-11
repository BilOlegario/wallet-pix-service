package br.com.walletpix.domain.exception;

public class InvalidHistoricalDateException extends DomainException {
    public InvalidHistoricalDateException() {
        super("A data de consulta não pode ser no futuro.");
    }
}
