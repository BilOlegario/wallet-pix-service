package br.com.walletpix.domain.repository;

public interface IdempotencyRepository {
    boolean isNotProcessed(String scope, String key);

    void markAsProcessed(String scope, String key);
}
