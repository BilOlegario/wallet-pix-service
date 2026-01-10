package br.com.walletpix.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para transação financeira")
public class TransactionRequestDto {

    @NotNull
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Schema(description = "Valor da transação", example = "100.00")
    private BigDecimal amount;
}
