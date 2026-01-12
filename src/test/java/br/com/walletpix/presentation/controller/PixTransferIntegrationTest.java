package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.domain.valueobject.PixStatus;
import br.com.walletpix.presentation.dto.BalanceResponseDto;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.PixTransferRequestDto;
import br.com.walletpix.presentation.dto.PixTransferResponseDto;
import br.com.walletpix.presentation.dto.PixWebhookRequestDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PixTransferIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void shouldExecuteFullPixFlowSuccessfully() {
                // 1. Setup: Create Wallet and Deposit 100.00
                ResponseEntity<CreateWalletResponseDto> walletResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = walletResponse.getBody().getId();

                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("100.00")), Void.class);

                // 2. Send Pix (50.00)
                String idempotencyKey = UUID.randomUUID().toString();
                PixTransferRequestDto request = new PixTransferRequestDto(
                                walletId, PixKeyType.CPF, "12345678901", new BigDecimal("50.00"));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Idempotency-Key", idempotencyKey);
                HttpEntity<PixTransferRequestDto> entity = new HttpEntity<>(request, headers);

                ResponseEntity<PixTransferResponseDto> transferResponse = restTemplate.postForEntity(
                                "/pix/transfers", entity, PixTransferResponseDto.class);

                assertThat(transferResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(transferResponse.getBody().getStatus()).isEqualTo(PixStatus.PENDING);
                String endToEndId = transferResponse.getBody().getEndToEndId();

                // 3. Verify Balance is deducted (50.00)
                ResponseEntity<BalanceResponseDto> balanceResponse = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance", BalanceResponseDto.class);
                assertThat(balanceResponse.getBody().getBalance()).isEqualByComparingTo("50.00");

                // 4. Send Webhook: CONFIRMED
                PixWebhookRequestDto webhookRequest = new PixWebhookRequestDto("evt_1", endToEndId,
                                PixStatus.CONFIRMED);
                ResponseEntity<Void> webhookResponse = restTemplate.postForEntity("/pix/webhook", webhookRequest,
                                Void.class);
                assertThat(webhookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // 5. Verify Balance is still 50.00
                balanceResponse = restTemplate.getForEntity("/wallets/" + walletId + "/balance",
                                BalanceResponseDto.class);
                assertThat(balanceResponse.getBody().getBalance()).isEqualByComparingTo("50.00");
        }

        @Test
        void shouldRollbackBalanceWhenPixIsRejected() {
                // 1. Setup: Create Wallet and Deposit 100.00
                ResponseEntity<CreateWalletResponseDto> walletResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = walletResponse.getBody().getId();

                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("100.00")), Void.class);

                // 2. Send Pix (40.00)
                String idempotencyKey = UUID.randomUUID().toString();
                PixTransferRequestDto request = new PixTransferRequestDto(
                                walletId, PixKeyType.EMAIL, "test@example.com", new BigDecimal("40.00"));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Idempotency-Key", idempotencyKey);
                HttpEntity<PixTransferRequestDto> entity = new HttpEntity<>(request, headers);

                ResponseEntity<PixTransferResponseDto> transferResponse = restTemplate.postForEntity(
                                "/pix/transfers", entity, PixTransferResponseDto.class);
                String endToEndId = transferResponse.getBody().getEndToEndId();

                // 3. Send Webhook: REJECTED
                PixWebhookRequestDto webhookRequest = new PixWebhookRequestDto("evt_2", endToEndId, PixStatus.REJECTED);
                restTemplate.postForEntity("/pix/webhook", webhookRequest, Void.class);

                // 4. Verify Balance is restored to 100.00
                ResponseEntity<BalanceResponseDto> balanceResponse = restTemplate.getForEntity(
                                "/wallets/" + walletId + "/balance", BalanceResponseDto.class);
                assertThat(balanceResponse.getBody().getBalance()).isEqualByComparingTo("100.00");
        }

        @Test
        void shouldPreventDuplicateTransfersWithSameIdempotencyKey() {
                // 1. Setup: Create Wallet and Deposit 100.00
                ResponseEntity<CreateWalletResponseDto> walletResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = walletResponse.getBody().getId();

                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("100.00")), Void.class);

                // 2. Send Pix First Time
                String idempotencyKey = "fixed-key-" + UUID.randomUUID();
                PixTransferRequestDto request = new PixTransferRequestDto(
                                walletId, PixKeyType.PHONE, "+5511999999999", new BigDecimal("10.00"));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Idempotency-Key", idempotencyKey);
                HttpEntity<PixTransferRequestDto> entity = new HttpEntity<>(request, headers);

                ResponseEntity<PixTransferResponseDto> response1 = restTemplate.postForEntity(
                                "/pix/transfers", entity, PixTransferResponseDto.class);
                assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

                // 3. Send Pix Second Time (Same Key)
                ResponseEntity<String> response2 = restTemplate.postForEntity(
                                "/pix/transfers", entity, String.class);

                // Should return error (as implemented in SendPixUseCaseImpl)
                assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                // Note: In a production app, we'd handle this better to return 200/201 with
                // stored result
        }

        @Test
        void shouldFailWhenInsufficientBalance() {
                // 1. Setup: Create Wallet with 10.00
                ResponseEntity<CreateWalletResponseDto> walletResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = walletResponse.getBody().getId();

                restTemplate.postForEntity("/wallets/" + walletId + "/deposit",
                                new TransactionRequestDto(new BigDecimal("10.00")), Void.class);

                // 2. Try to Send Pix of 20.00
                PixTransferRequestDto request = new PixTransferRequestDto(
                                walletId, PixKeyType.CPF, "12345678901", new BigDecimal("20.00"));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Idempotency-Key", UUID.randomUUID().toString());
                HttpEntity<PixTransferRequestDto> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(
                                "/pix/transfers", entity, String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }
}
