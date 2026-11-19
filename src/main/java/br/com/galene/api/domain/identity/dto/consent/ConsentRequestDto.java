package br.com.galene.api.domain.identity.dto.consent;

import br.com.galene.api.domain.identity.enums.TermType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ConsentRequestDto(
        @Schema(example = "[\"DATA_USE\", \"LOCATION\"]")
        @NotEmpty(message = "Informe ao menos um termo")
        List<@NotNull(message = "Termo inválido") TermType> termTypes
) { }
