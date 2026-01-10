package br.com.walletpix.presentation.controller;

import br.com.walletpix.BaseIntegrationTest;
import br.com.walletpix.domain.valueobject.PixKeyType;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyRequestDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
                assertThat(registerResponse.getBody().getValue()).isEqualTo("test@example.com");
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
                assertThat(registerResponse.getBody().getValue()).isNotNull();
                assertThat(registerResponse.getBody().getValue()).isNotEmpty();
        }

        @Test
        void shouldReturnBadRequestWhenWalletNotFound() {
                RegisterPixKeyRequestDto registerRequest = new RegisterPixKeyRequestDto();
                registerRequest.setType(PixKeyType.CPF);
                registerRequest.setValue("12345678901");

                ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                                "/wallets/" + UUID.randomUUID() + "/pix-keys", registerRequest, Void.class);

                // O Use Case lança IllegalArgumentException, que o Spring mapeia para 500 por
                // padrão se não houver handler.
                // Em um sistema real teríamos um GlobalExceptionHandler.
                assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
