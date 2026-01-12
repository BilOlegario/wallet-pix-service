package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.presentation.dto.BalanceResponseDto;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldMaintainBalanceConsistencyUnderConcurrentDeposits() throws InterruptedException {
        // 1. Setup: Create Wallet
        ResponseEntity<CreateWalletResponseDto> walletResponse = restTemplate.postForEntity(
                "/wallets", null, CreateWalletResponseDto.class);
        UUID walletId = walletResponse.getBody().getId();

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 2. Execute 10 concurrent deposits of 10.00 each
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    latch.await(); // Wait for signal to start all at once
                    ResponseEntity<Void> response = restTemplate.postForEntity(
                            "/wallets/" + walletId + "/deposit",
                            new TransactionRequestDto(new BigDecimal("10.00")),
                            Void.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        latch.countDown(); // Start!
        executorService.shutdown();
        executorService.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);

        // 3. Verify consistency
        ResponseEntity<BalanceResponseDto> balanceResponse = restTemplate.getForEntity(
                "/wallets/" + walletId + "/balance", BalanceResponseDto.class);

        BigDecimal expectedBalance = new BigDecimal("10.00").multiply(new BigDecimal(successCount.get()));
        assertThat(balanceResponse.getBody().getBalance()).isEqualByComparingTo(expectedBalance);

        System.out.println("Success: " + successCount.get() + ", Failures: " + failureCount.get());
        // With Optimistic Locking and NO retries on deposit (yet), we expect some
        // failures.
        // If we implement retries on deposit too, successCount should be 10.
    }
}
