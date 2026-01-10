package br.com.walletpix.presentation.dto;

import br.com.walletpix.domain.valueobject.PixKeyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPixKeyResponseDto {
    private PixKeyType type;
    private String key;
}
