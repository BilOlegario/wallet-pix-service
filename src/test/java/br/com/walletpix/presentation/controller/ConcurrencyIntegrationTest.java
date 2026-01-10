package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.domain.entity.Wallet;
import br.com.walletpix.domain.repository.WalletRepository;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldHandleConcurrentWithdrawalsWithOptimisticLocking() throws Exception {
        // 1. Criar Carteira com saldo inicial
        ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                "/wallets", null, CreateWalletResponseDto.class);
        UUID walletId = createResponse.getBody().getId();

        // Depósito inicial de 100.00
        restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                new TransactionRequestDto(new BigDecimal("100.00")), Void.class);

        // 2. Simular 10 saques simultâneos de 10.00
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                ResponseEntity<Void> response = restTemplate.postForEntity(
                        "/wallets/" + walletId + "/withdraw",
                        new TransactionRequestDto(new BigDecimal("10.00")), Void.class);

                if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                    successCount.incrementAndGet();
                } else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                    // Esperamos erros de lock aqui (500 por padrão no Spring sem tratamento global)
                    failureCount.incrementAndGet();
                }
            }, executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        executor.shutdown();

        // 3. Validar consistência
        Wallet finalWallet = walletRepository.findById(walletId).get();

        // O saldo final deve ser: 100.00 - (sucessos * 10.00)
        BigDecimal expectedBalance = new BigDecimal("100.00")
                .subtract(new BigDecimal(successCount.get()).multiply(new BigDecimal("10.00")));

        assertThat(finalWallet.getBalance().getAmount()).isEqualByComparingTo(expectedBalance);
        assertThat(successCount.get()).isGreaterThan(0);
        assertThat(failureCount.get()).isGreaterThan(0); // Deve haver falhas de concorrência

        System.out.println("Sucessos: " + successCount.get());
        System.out.println("Falhas (Locking): " + failureCount.get());
        System.out.println("Saldo Final: " + finalWallet.getBalance());
    }
}
