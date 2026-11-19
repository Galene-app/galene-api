package br.com.galene.api.domain.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record DailyReportDto(
        LocalDate date,
        Integer readings,
        Long monitoredSeconds,
        BigDecimal averageDb,
        String averageLevel,
        BigDecimal peakDb,
        OffsetDateTime peakAt,
        String peakLevel,
        List<LevelTimeDto> timeByLevel,
        List<LocationExposureDto> intenseLocations
) { }
