package br.com.galene.api.domain.monitoring.dto;

import br.com.galene.api.domain.monitoring.enums.LocationType;
import br.com.galene.api.domain.monitoring.enums.MeasurementSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record NoiseReadingDto(
        @Schema(example = "72.45", description = "0 a 140 dB; arredondado para 2 casas")
        @NotNull(message = "Informe o valor em dB")
        BigDecimal dbValue,

        @Schema(example = "2026-11-18T14:32:05-03:00", description = "Horário do celular, com fuso")
        @NotNull(message = "Informe o horário da leitura")
        OffsetDateTime measuredAt,

        @Schema(example = "TRANSPORT", description = "Opcional; só é gravado com consentimento de localização e nos níveis Intenso e Crítico")
        LocationType locationType,

        @Schema(example = "PHONE", description = "Opcional; padrão PHONE")
        MeasurementSource source
) { }
