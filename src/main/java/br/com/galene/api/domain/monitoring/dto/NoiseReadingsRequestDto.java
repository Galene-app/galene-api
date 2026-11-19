package br.com.galene.api.domain.monitoring.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NoiseReadingsRequestDto(
        @NotEmpty(message = "Envie ao menos uma leitura")
        List<@Valid NoiseReadingDto> readings
) { }
