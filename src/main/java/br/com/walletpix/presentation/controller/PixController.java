package br.com.walletpix.presentation.controller;

import br.com.walletpix.application.usecase.ProcessPixWebhookUseCase;
import br.com.walletpix.application.usecase.SendPixUseCase;
import br.com.walletpix.domain.entity.PixTransfer;
import br.com.walletpix.domain.valueobject.Money;
import br.com.walletpix.presentation.dto.PixTransferRequestDto;
import br.com.walletpix.presentation.dto.PixTransferResponseDto;
import br.com.walletpix.presentation.dto.PixWebhookRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
@Tag(name = "Pix", description = "Endpoints para operações Pix e Idempotência")
public class PixController {

    private final SendPixUseCase sendPixUseCase;
    private final ProcessPixWebhookUseCase processPixWebhookUseCase;

    @PostMapping("/transfers")
    @Operation(summary = "Enviar Pix", description = "Realiza o envio de um Pix com controle de idempotência.")
    @ApiResponse(responseCode = "201", description = "Transferência iniciada com sucesso")
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente ou regra de negócio violada")
    public ResponseEntity<PixTransferResponseDto> sendPix(
            @Parameter(description = "Chave de idempotência obrigatória") @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PixTransferRequestDto request) {

        PixTransfer transfer = sendPixUseCase.execute(
                request.getSenderWalletId(),
                request.getReceiverKeyType(),
                request.getReceiverKeyValue(),
                new Money(request.getAmount()),
                idempotencyKey);

        PixTransferResponseDto response = new PixTransferResponseDto(
                transfer.getEndToEndId(),
                transfer.getStatus(),
                transfer.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Webhook Pix", description = "Simula o recebimento de uma confirmação ou rejeição do PSP.")
    @ApiResponse(responseCode = "200", description = "Webhook processado com sucesso")
    public ResponseEntity<Void> processWebhook(@Valid @RequestBody PixWebhookRequestDto request) {
        processPixWebhookUseCase.execute(request.getEventId(), request.getEndToEndId(), request.getStatus());
        return ResponseEntity.ok().build();
    }
}
