package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do envio de Pix")
public class PixTransferResponseDto {

    @Schema(description = "Identificador End-to-End da transferência", example = "E12345678202301011200abcde123456")
    private String endToEndId;

    @Schema(description = "Status atual da transferência", example = "PENDING")
    private PixStatus status;

    @Schema(description = "Data de criação da transferência")
    private LocalDateTime createdAt;
}
