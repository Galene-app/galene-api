package br.com.galene.api.domain.monitoring.dto;

import br.com.galene.api.domain.monitoring.entity.MeasurementEntity;
import br.com.galene.api.domain.monitoring.enums.LocationType;
import br.com.galene.api.domain.monitoring.enums.MeasurementSource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record NoiseReadingResponseDto(
        Long id,
        BigDecimal dbValue,
        String level,
        String colorHex,
        OffsetDateTime measuredAt,
        LocationType locationType,
        MeasurementSource source
) {

    public NoiseReadingResponseDto(MeasurementEntity measurement, ZoneId zone) {
        this(measurement.getId(), measurement.getDbValue(), measurement.getLevel().getLevelName(), measurement.getLevel().getColorHex(),
                measurement.getMeasuredAt().atZone(zone).toOffsetDateTime(), measurement.getLocationType(), measurement.getSource());
    }
}
