package br.com.walletpix.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resposta contendo as informações de saldo da carteira")
public class BalanceResponseDto {
    @Schema(description = "ID único da carteira", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID walletId;

    @Schema(description = "Saldo da carteira no momento consultado", example = "150.00")
    private BigDecimal balance;

    @Schema(description = "Data e hora em que o saldo foi calculado", example = "2026-01-11T13:00:00")
    private LocalDateTime timestamp;
}
