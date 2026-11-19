package br.com.galene.api.domain.identity.dto.user;

import br.com.galene.api.domain.identity.enums.ColorblindMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Schema(example = "Maria Silva")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String fullName,

        @NotNull(message = "Modo escuro é obrigatório")
        Boolean darkMode,

        @NotNull(message = "Modo daltônico é obrigatório")
        ColorblindMode colorblindMode,

        @NotNull(message = "Leitura em voz alta é obrigatória")
        Boolean ttsEnabled
) { }
