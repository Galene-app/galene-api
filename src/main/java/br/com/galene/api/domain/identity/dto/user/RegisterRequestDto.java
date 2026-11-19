package br.com.galene.api.domain.identity.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequestDto(
        @Schema(example = "Maria Silva")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String fullName,

        @Schema(example = "1998-04-12")
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento não pode ser futura")
        LocalDate birthDate,

        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @Schema(example = "Senha@123", description = "8 a 64 caracteres, com maiúscula, minúscula, número e símbolo")
        @NotBlank(message = "Senha é obrigatória")
        String password,

        @Schema(description = "Aceite dos termos de uso da conta (versão vigente)")
        @NotNull(message = "O aceite dos termos é obrigatório")
        @AssertTrue(message = "É necessário aceitar os termos de uso para criar a conta")
        Boolean acceptTerms
) {
    @Override
    public String toString() {
        return "RegisterRequestDto[birthDate=" + birthDate + ", acceptTerms=" + acceptTerms + "]";
    }
}
