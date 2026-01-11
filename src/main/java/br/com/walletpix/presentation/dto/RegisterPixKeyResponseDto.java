package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do registro de chave Pix")
public class RegisterPixKeyResponseDto {
    @Schema(description = "Tipo da chave Pix registrada")
    private PixKeyType type;

    @Schema(description = "Valor da chave Pix registrada (gerado automaticamente se for EVP)")
    private String key;
}
