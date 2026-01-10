package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyRequestDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import br.com.walletpix.presentation.exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WalletControllerIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void shouldCreateWalletAndRegisterPixKey() {
                // 1. Create Wallet
                ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);

                assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                UUID walletId = createResponse.getBody().getId();
                assertThat(walletId).isNotNull();

                // 2. Register Pix Key
                RegisterPixKeyRequestDto registerRequest = new RegisterPixKeyRequestDto();
                registerRequest.setType(PixKeyType.EMAIL);
                registerRequest.setValue("test@example.com");

                ResponseEntity<RegisterPixKeyResponseDto> registerResponse = restTemplate.postForEntity(
                                "/wallets/" + walletId + "/pix-keys", registerRequest, RegisterPixKeyResponseDto.class);

                assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(registerResponse.getBody().getType()).isEqualTo(PixKeyType.EMAIL);
                assertThat(registerResponse.getBody().getKey()).isEqualTo("test@example.com");
        }

        @Test
        void shouldRegisterEvpKeyWithGeneratedValues() {
                // 1. Create Wallet
                ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = createResponse.getBody().getId();

                // 2. Register EVP Key (no value provided)
                RegisterPixKeyRequestDto registerRequest = new RegisterPixKeyRequestDto();
                registerRequest.setType(PixKeyType.EVP);

                ResponseEntity<RegisterPixKeyResponseDto> registerResponse = restTemplate.postForEntity(
                                "/wallets/" + walletId + "/pix-keys", registerRequest, RegisterPixKeyResponseDto.class);

                assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(registerResponse.getBody().getType()).isEqualTo(PixKeyType.EVP);
                assertThat(registerResponse.getBody().getKey()).isNotNull();
                assertThat(registerResponse.getBody().getKey()).isNotEmpty();
        }

        @Test
        void shouldReturnNotFoundWhenWalletNotFoundOnPixKeyRegistration() {
                RegisterPixKeyRequestDto registerRequest = new RegisterPixKeyRequestDto();
                registerRequest.setType(PixKeyType.CPF);
                registerRequest.setValue("12345678901");

                ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                                "/wallets/" + UUID.randomUUID() + "/pix-keys", registerRequest, ErrorResponse.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(response.getBody().getMessage()).isEqualTo("Carteira não encontrada");
        }

        @Test
        void shouldReturnNotFoundWhenWalletNotFoundOnDeposit() {
                TransactionRequestDto request = new TransactionRequestDto(new BigDecimal("10.00"));

                ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                                "/wallets/" + UUID.randomUUID() + "/deposit", request, ErrorResponse.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(response.getBody().getMessage()).isEqualTo("Carteira não encontrada");
        }

        @Test
        void shouldReturnUnprocessableEntityWhenInsufficientBalance() {
                // 1. Create Wallet (starts with zero balance)
                ResponseEntity<CreateWalletResponseDto> createResponse = restTemplate.postForEntity(
                                "/wallets", null, CreateWalletResponseDto.class);
                UUID walletId = createResponse.getBody().getId();

                // 2. Try to withdraw 10.00 from empty wallet
                TransactionRequestDto request = new TransactionRequestDto(new BigDecimal("10.00"));

                ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                                "/wallets/" + walletId + "/withdraw", request, ErrorResponse.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                assertThat(response.getBody().getMessage()).isEqualTo("Saldo insuficiente para realizar a operação");
        }
}
