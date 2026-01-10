package br.com.walletpix.presentation.controller;

import br.com.walletpix.domain.entity.PixKey;
import br.com.walletpix.application.usecase.CreateWalletUseCase;
import br.com.walletpix.application.usecase.RegisterPixKeyUseCase;
import br.com.walletpix.application.usecase.DepositUseCase;
import br.com.walletpix.application.usecase.WithdrawUseCase;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyRequestDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallets", description = "Gestão de carteiras e chaves Pix")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final RegisterPixKeyUseCase registerPixKeyUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova carteira", description = "Cria uma carteira vazia com saldo zero.")
    public ResponseEntity<CreateWalletResponseDto> createWallet() {
        UUID walletId = createWalletUseCase.execute();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateWalletResponseDto(walletId));
    }

    @PostMapping("/{id}/pix-keys")
    @Operation(summary = "Registra uma chave Pix", description = "Vincula uma nova chave Pix a uma carteira existente.")
    public ResponseEntity<RegisterPixKeyResponseDto> registerPixKey(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterPixKeyRequestDto request) {

        PixKey pixKey = registerPixKeyUseCase.execute(id, request.getType(), request.getValue());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterPixKeyResponseDto(pixKey.getType(), pixKey.getValue()));
    }

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Realiza um depósito", description = "Adiciona saldo a uma carteira e gera um registro no Ledger.")
    public ResponseEntity<Void> deposit(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequestDto request) {

        depositUseCase.execute(id, new Money(request.getAmount()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Realiza um saque", description = "Remove saldo de uma carteira, gera um registro no Ledger e valida saldo insuficiente.")
    public ResponseEntity<Void> withdraw(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequestDto request) {

        withdrawUseCase.execute(id, new Money(request.getAmount()));
        return ResponseEntity.noContent().build();
    }
}
