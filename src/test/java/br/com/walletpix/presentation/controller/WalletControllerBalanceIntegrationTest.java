package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.presentation.dto.BalanceResponseDto;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WalletControllerBalanceIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void shouldQueryCurrentAndHistoricalBalance() throws InterruptedException {
                // 1. Create Wallet
                ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = createResponse.getBody().getId();

                // 2. Initial balance should be zero
                ResponseEntity<BalanceResponseDto> initialBalanceResponse = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance", BalanceResponseDto.class);
                assertThat(initialBalanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(initialBalanceResponse.getBody().getBalance()).isEqualByComparingTo("0.00");

                // 3. First Deposit: 100.00
                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("100.00")), Void.class);

                LocalDateTime afterFirstDeposit = LocalDateTime.now();
                Thread.sleep(100);

                // 4. Withdrawal: 30.00 (Balance should be 70.00)
                restTemplate.postForEntity("/wallets/" + walletId + "/withdraw",
                                new TransactionRequestDto(new BigDecimal("30.00")), Void.class);

                LocalDateTime afterWithdrawal = LocalDateTime.now();
                Thread.sleep(100);

                // 5. Second Deposit: 50.00 (Balance should be 120.00)
                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("50.00")), Void.class);

                // 6. Query Current Balance (should be 120.00)
                ResponseEntity<BalanceResponseDto> currentBalanceResponse = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance", BalanceResponseDto.class);
                assertThat(currentBalanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(currentBalanceResponse.getBody().getBalance()).isEqualByComparingTo("120.00");

                // 7. Query Historical Balance (after first deposit, before withdrawal)
                String atFirstDeposit = afterFirstDeposit.format(DateTimeFormatter.ISO_DATE_TIME);
                ResponseEntity<BalanceResponseDto> hist1 = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance?at=" + atFirstDeposit, BalanceResponseDto.class);
                assertThat(hist1.getBody().getBalance()).isEqualByComparingTo("100.00");

                // 8. Query Historical Balance (after withdrawal, before second deposit)
                String atWithdrawal = afterWithdrawal.format(DateTimeFormatter.ISO_DATE_TIME);
                ResponseEntity<BalanceResponseDto> hist2 = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance?at=" + atWithdrawal, BalanceResponseDto.class);
                assertThat(hist2.getBody().getBalance()).isEqualByComparingTo("70.00");
        }

        @Test
        void shouldReturnNotFoundWhenQueryingBalanceOfNonExistentWallet() {
                ResponseEntity<Void> response = restTemplate.getForEntity(
                                "/wallets/" + UUID.randomUUID() + "/balance", Void.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void shouldReturnErrorWhenQueryingBalanceWithFutureDate() {
                // 1. Create Wallet
                ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = createResponse.getBody().getId();

                // 2. Query with Future Date (now + 1 day)
                String futureDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);

                ResponseEntity<String> response = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance?at=" + futureDate, String.class);

                // DomainException should map to 422
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }
}
