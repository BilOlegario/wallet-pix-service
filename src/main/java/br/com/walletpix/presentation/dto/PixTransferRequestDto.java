package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para envio de Pix")
public class PixTransferRequestDto {

    @NotNull
    @Schema(description = "ID da carteira de origem", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID senderWalletId;

    @NotNull
    @Schema(description = "Tipo da chave Pix de destino", example = "CPF")
    private PixKeyType receiverKeyType;

    @NotBlank
    @Schema(description = "Valor da chave Pix de destino", example = "12345678901")
    private String receiverKeyValue;

    @NotNull
    @Positive
    @Schema(description = "Valor da transferência", example = "100.00")
    private BigDecimal amount;
}
