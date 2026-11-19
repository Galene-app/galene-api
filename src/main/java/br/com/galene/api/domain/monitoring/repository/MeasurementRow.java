package br.com.galene.api.domain.monitoring.repository;

import br.com.galene.api.domain.monitoring.enums.LocationType;
import br.com.galene.api.domain.monitoring.enums.MeasurementSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MeasurementRow(
        Long patientId,
        Long levelId,
        LocationType locationType,
        BigDecimal dbValue,
        LocalDateTime measuredAt,
        MeasurementSource source
) { }
