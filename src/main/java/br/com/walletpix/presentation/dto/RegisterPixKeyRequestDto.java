package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para registro de chave Pix")
public class RegisterPixKeyRequestDto {
    @NotNull
    @Schema(description = "Tipo da chave Pix", example = "EMAIL")
    private PixKeyType type;

    @Schema(description = "Valor da chave Pix. Obrigatório para todos os tipos exceto EVP.", example = "test@example.com")
    private String value; // Optional for EVP
}
