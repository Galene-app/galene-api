package br.com.galene.api.domain.identity.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDto(
        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email
) {
    @Override
    public String toString() {
        return "ForgotPasswordRequestDto[***]";
    }
}
