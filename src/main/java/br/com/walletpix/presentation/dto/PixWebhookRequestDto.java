package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição do Webhook de retorno do Pix")
public class PixWebhookRequestDto {

    @NotBlank
    @Schema(description = "Identificador único do evento no BC", example = "evt_123456")
    private String eventId;

    @NotBlank
    @Schema(description = "Identificador End-to-End da transferência", example = "E12345678202301011200abcde123456")
    private String endToEndId;

    @NotNull
    @Schema(description = "Novo status da transferência (CONFIRMED ou REJECTED)", example = "CONFIRMED")
    private PixStatus status;
}
