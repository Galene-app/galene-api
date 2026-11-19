package br.com.galene.api.domain.identity.dto.consent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PermissionsDto(
        @Schema(description = "Obrigatória: sem microfone o app não mede o ruído", example = "true")
        @NotNull(message = "Informe a permissão de microfone")
        Boolean microphone,

        @Schema(description = "Opcional: qualquer fone externo", example = "false")
        @NotNull(message = "Informe a permissão de Bluetooth")
        Boolean bluetooth,

        @Schema(description = "Opcional: usa o consentimento de localização", example = "true")
        @NotNull(message = "Informe a permissão de localização")
        Boolean location
) { }
