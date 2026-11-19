package br.com.galene.api.domain.monitoring.repository;

import br.com.galene.api.domain.monitoring.enums.LocationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReadingPoint(
        BigDecimal dbValue,
        LocalDateTime measuredAt,
        LocationType locationType,
        Long levelId
) { }
