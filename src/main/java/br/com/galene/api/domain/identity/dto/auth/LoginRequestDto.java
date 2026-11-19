package br.com.galene.api.domain.identity.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password
) {
    @Override
    public String toString() {
        return "LoginRequestDto[***]";
    }
}
