package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixKeyType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPixKeyRequestDto {
    @NotNull
    private PixKeyType type;

    private String value; // Optional for EVP
}
