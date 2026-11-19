package br.com.galene.api.domain.identity.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @Schema(description = "Senha atual (ou a temporária recebida por e-mail)")
        @NotBlank(message = "Senha atual é obrigatória")
        String currentPassword,

        @Schema(example = "NovaSenha@123", description = "8 a 64 caracteres, com maiúscula, minúscula, número e símbolo")
        @NotBlank(message = "Nova senha é obrigatória")
        String newPassword
) {
    @Override
    public String toString() {
        return "ChangePasswordRequestDto[***]";
    }
}
