package br.com.galene.api.domain.identity.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TwoFactorVerifyRequestDto(
        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @Schema(example = "042137")
        @NotBlank(message = "Código é obrigatório")
        @Pattern(regexp = "\\d{6}", message = "O código deve ter 6 dígitos")
        String code
) {
    @Override
    public String toString() {
        return "TwoFactorVerifyRequestDto[***]";
    }
}
