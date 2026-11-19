package br.com.galene.api.domain.identity.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "Refresh token é obrigatório")
        String refreshToken
) {
    @Override
    public String toString() {
        return "RefreshTokenRequestDto[***]";
    }
}
