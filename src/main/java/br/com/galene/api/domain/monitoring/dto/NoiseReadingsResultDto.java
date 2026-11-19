package br.com.galene.api.domain.monitoring.dto;

import java.math.BigDecimal;

public record NoiseReadingsResultDto(
        Integer saved,
        Integer criticalReadings,
        BigDecimal peakDb,
        String peakLevel
) { }
