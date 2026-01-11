package br.com.walletpix.presentation.controller;

import br.com.walletpix.domain.entity.PixKey;
import br.com.walletpix.application.usecase.CreateWalletUseCase;
import br.com.walletpix.application.usecase.RegisterPixKeyUseCase;
import br.com.walletpix.application.usecase.DepositUseCase;
import br.com.walletpix.application.usecase.WithdrawUseCase;
import br.com.walletpix.application.usecase.GetBalanceUseCase;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.presentation.dto.BalanceResponseDto;
import br.com.walletpix.presentation.dto.CreateWalletResponseDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyRequestDto;
import br.com.walletpix.presentation.dto.RegisterPixKeyResponseDto;
import br.com.walletpix.presentation.dto.TransactionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    private final GetBalanceUseCase getBalanceUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova carteira", description = "Cria uma carteira vazia com saldo zero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carteira criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<CreateWalletResponseDto> createWallet() {
        UUID walletId = createWalletUseCase.execute();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateWalletResponseDto(walletId));
    }

    @PostMapping("/{id}/pix-keys")
    @Operation(summary = "Registra uma chave Pix", description = "Vincula uma nova chave Pix a uma carteira existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chave Pix registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carteira não encontrada"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (ex: chave já existente)")
    })
    public ResponseEntity<RegisterPixKeyResponseDto> registerPixKey(
            @PathVariable @Parameter(description = "ID da carteira") UUID id,
            @Valid @RequestBody RegisterPixKeyRequestDto request) {

        PixKey pixKey = registerPixKeyUseCase.execute(id, request.getType(), request.getValue());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterPixKeyResponseDto(pixKey.getType(), pixKey.getValue()));
    }

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Realiza um depósito", description = "Adiciona saldo a uma carteira e gera um registro no Ledger.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    public ResponseEntity<Void> deposit(
            @PathVariable @Parameter(description = "ID da carteira") UUID id,
            @Valid @RequestBody TransactionRequestDto request) {

        depositUseCase.execute(id, new Money(request.getAmount()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Realiza um saque", description = "Remove saldo de uma carteira, gera um registro no Ledger e valida saldo insuficiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carteira não encontrada"),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    })
    public ResponseEntity<Void> withdraw(
            @PathVariable @Parameter(description = "ID da carteira") UUID id,
            @Valid @RequestBody TransactionRequestDto request) {

        withdrawUseCase.execute(id, new Money(request.getAmount()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Consulta o saldo", description = "Consulta o saldo atual da carteira ou o saldo em um momento específico (at query param).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo consultado com sucesso", content = @Content(schema = @Schema(implementation = BalanceResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    public ResponseEntity<BalanceResponseDto> getBalance(
            @PathVariable @Parameter(description = "ID da carteira") UUID id,
            @RequestParam(required = false) @Parameter(description = "Timestamp para consulta de saldo histórico (ISO 8601). Ex: 2026-01-11T13:00:00") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime at) {

        Money balance = getBalanceUseCase.execute(id, at);
        return ResponseEntity
                .ok(new BalanceResponseDto(id, balance.getAmount(), at != null ? at : LocalDateTime.now()));
    }
}
