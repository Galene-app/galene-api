package br.com.galene.api.domain.reporting.service;

import br.com.galene.api.domain.monitoring.entity.IntensityLevelEntity;
import br.com.galene.api.domain.monitoring.enums.LocationType;
import br.com.galene.api.domain.monitoring.repository.ReadingPoint;
import br.com.galene.api.domain.monitoring.service.NoiseReadingService;
import br.com.galene.api.domain.reporting.constants.ReportMessages;
import br.com.galene.api.domain.reporting.dto.DailyReportDto;
import br.com.galene.api.domain.reporting.dto.LevelTimeDto;
import br.com.galene.api.domain.reporting.dto.LocationExposureDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DailyReportCalculator {

    public static final Duration MAX_READING_DURATION = Duration.ofSeconds(60);
    private static final int DB_SCALE = 2;
    private static final int PERCENT_SCALE = 1;
    private static final double DECIBEL_FACTOR = 10.0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public DailyReportDto calculate(LocalDate date, List<ReadingPoint> readings, List<IntensityLevelEntity> levels, ZoneId zone) {
        var levelsById = levels.stream().collect(Collectors.toMap(IntensityLevelEntity::getId, Function.identity()));
        var dayEnd = date.plusDays(1).atStartOfDay();
        var nanosByLevel = new HashMap<Long, Long>();
        var nanosByLocation = new EnumMap<LocationType, Long>(LocationType.class);
        double weightedEnergy = 0;
        long totalNanos = 0;
        ReadingPoint peak = null;

        for (int index = 0; index < readings.size(); index++) {
            var reading = readings.get(index);
            var next = index + 1 < readings.size() ? readings.get(index + 1).measuredAt() : dayEnd;
            long nanos = Math.max(1, Math.min(Duration.between(reading.measuredAt(), next).toNanos(), MAX_READING_DURATION.toNanos()));

            totalNanos += nanos;
            weightedEnergy += nanos * Math.pow(DECIBEL_FACTOR, reading.dbValue().doubleValue() / DECIBEL_FACTOR);
            nanosByLevel.merge(reading.levelId(), nanos, Long::sum);
            if (reading.locationType() != null && levelsById.get(reading.levelId()).startsAtOrAbove(NoiseReadingService.LOCATION_MIN_DB))
                nanosByLocation.merge(reading.locationType(), nanos, Long::sum);
            if (peak == null || reading.dbValue().compareTo(peak.dbValue()) > 0) peak = reading;
        }

        var average = BigDecimal.valueOf(DECIBEL_FACTOR * Math.log10(weightedEnergy / totalNanos)).setScale(DB_SCALE, RoundingMode.HALF_UP);
        long total = totalNanos;
        var timeByLevel = levels.stream().map(level -> {
            long nanos = nanosByLevel.getOrDefault(level.getId(), 0L);
            return new LevelTimeDto(level.getLevelName(), level.getColorHex(), toSeconds(nanos), percent(nanos, total));
        }).toList();
        var intenseLocations = nanosByLocation.entrySet().stream()
                .sorted(Map.Entry.<LocationType, Long>comparingByValue().reversed())
                .map(entry -> new LocationExposureDto(entry.getKey(), toSeconds(entry.getValue())))
                .toList();

        return new DailyReportDto(
                date,
                readings.size(),
                toSeconds(totalNanos),
                average,
                classify(levels, average).getLevelName(),
                peak.dbValue(),
                peak.measuredAt().atZone(zone).toOffsetDateTime(),
                levelsById.get(peak.levelId()).getLevelName(),
                timeByLevel,
                intenseLocations);
    }

    private static IntensityLevelEntity classify(List<IntensityLevelEntity> levels, BigDecimal dbValue) {
        return levels.stream().filter(level -> level.contains(dbValue)).findFirst()
                .orElseThrow(() -> new IllegalStateException(ReportMessages.LEVEL_NOT_FOUND + dbValue));
    }

    private static long toSeconds(long nanos) {
        return Math.round(nanos / NANOS_PER_SECOND);
    }

    private static BigDecimal percent(long nanos, long totalNanos) {
        return BigDecimal.valueOf(nanos).multiply(HUNDRED).divide(BigDecimal.valueOf(totalNanos), PERCENT_SCALE, RoundingMode.HALF_UP);
    }
}
