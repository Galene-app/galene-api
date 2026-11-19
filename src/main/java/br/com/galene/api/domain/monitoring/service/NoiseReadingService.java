package br.com.galene.api.domain.monitoring.service;

import br.com.galene.api.common.model.ErrorResponse.ErrorDetail;
import br.com.galene.api.common.model.PageResponse;
import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.service.ConsentService;
import br.com.galene.api.domain.monitoring.constants.MonitoringMessages;
import br.com.galene.api.domain.monitoring.dto.*;
import br.com.galene.api.domain.monitoring.dto.event.CriticalExposureEvent;
import br.com.galene.api.domain.monitoring.entity.IntensityLevelEntity;
import br.com.galene.api.domain.monitoring.enums.MeasurementSource;
import br.com.galene.api.domain.monitoring.exception.InvalidNoiseReadingsException;
import br.com.galene.api.domain.monitoring.exception.InvalidPeriodException;
import br.com.galene.api.domain.monitoring.exception.TooManyReadingsException;
import br.com.galene.api.domain.monitoring.repository.IntensityLevelRepository;
import br.com.galene.api.domain.monitoring.repository.MeasurementRepository;
import br.com.galene.api.domain.monitoring.repository.MeasurementRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class NoiseReadingService {

    public static final int MAX_READINGS = 500;
    public static final Duration FUTURE_TOLERANCE = Duration.ofSeconds(60);
    public static final Duration DEFAULT_PERIOD = Duration.ofHours(24);
    public static final BigDecimal MIN_DB = new BigDecimal("0.00");
    public static final BigDecimal MAX_DB = new BigDecimal("140.00");
    public static final BigDecimal LOCATION_MIN_DB = new BigDecimal("70.00");
    public static final BigDecimal CRITICAL_MIN_DB = new BigDecimal("85.00");
    private static final int DB_SCALE = 2;
    private static final String DB_VALUE_FIELD = "dbValue";
    private static final String MEASURED_AT_FIELD = "measuredAt";
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "measuredAt");

    private final MeasurementRepository measurementRepository;
    private final IntensityLevelRepository levelRepository;
    private final PatientRepository patientRepository;
    private final ConsentService consentService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<NoiseLevelDto> getNoiseLevels() {
        return levelRepository.findAllByOrderByDbMinAsc().stream().map(NoiseLevelDto::new).toList();
    }

    @Transactional
    public NoiseReadingsResultDto saveReadings(Long userId, NoiseReadingsRequestDto request) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        if (request.readings().size() > MAX_READINGS)
            throw new TooManyReadingsException(MonitoringMessages.TOO_MANY_READINGS.formatted(MAX_READINGS));

        var patient = findPatient(userId);
        var readings = request.readings().stream().map(this::normalize).toList();
        validate(patient, readings);

        var levels = levelRepository.findAllByOrderByDbMinAsc();
        var keepLocation = consentService.hasConsent(userId, TermType.LOCATION);
        var rows = readings.stream().map(reading -> toRow(patient, reading, classify(levels, reading.dbValue()), keepLocation)).toList();
        measurementRepository.insertMeasurements(rows);

        var peak = readings.stream().max(Comparator.comparing(NormalizedReading::dbValue)).orElseThrow();
        var criticalTimes = readings.stream().filter(reading -> reading.dbValue().compareTo(CRITICAL_MIN_DB) >= 0)
                .map(NormalizedReading::measuredAt).toList();
        if (!criticalTimes.isEmpty()) raiseCriticalExposure(userId, patient, peak, criticalTimes);

        log.info("Usuário {} enviou {} leitura(s), {} crítica(s)", userId, rows.size(), criticalTimes.size());
        return new NoiseReadingsResultDto(rows.size(), criticalTimes.size(), peak.dbValue(), classify(levels, peak.dbValue()).getLevelName());
    }

    @Transactional(readOnly = true)
    public PageResponse<NoiseReadingResponseDto> getReadings(Long userId, OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        var end = to == null ? LocalDateTime.now(clock) : toLocal(to);
        var start = from == null ? end.minus(DEFAULT_PERIOD) : toLocal(from);
        if (start.isAfter(end))
            throw new InvalidPeriodException(MonitoringMessages.INVALID_PERIOD);

        var page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), NEWEST_FIRST);
        var readings = measurementRepository.findByPatientIdAndMeasuredAtBetween(findPatient(userId).getId(), start, end, page);
        return new PageResponse<>(readings).map(measurement -> new NoiseReadingResponseDto(measurement, clock.getZone()));
    }

    private void validate(PatientEntity patient, List<NormalizedReading> readings) {
        var latestAllowed = LocalDateTime.now(clock).plus(FUTURE_TOLERANCE);
        var errors = new ArrayList<ErrorDetail>();
        var seen = new HashSet<LocalDateTime>();

        for (int index = 0; index < readings.size(); index++) {
            var reading = readings.get(index);
            if (reading.dbValue().compareTo(MIN_DB) < 0 || reading.dbValue().compareTo(MAX_DB) > 0)
                errors.add(detail(index, DB_VALUE_FIELD, MonitoringMessages.DB_OUT_OF_RANGE));
            if (reading.measuredAt().isAfter(latestAllowed))
                errors.add(detail(index, MEASURED_AT_FIELD, MonitoringMessages.FUTURE_READING));
            if (!seen.add(reading.measuredAt()))
                errors.add(detail(index, MEASURED_AT_FIELD, MonitoringMessages.DUPLICATED_IN_BATCH));
        }

        var alreadySaved = findSavedTimes(patient, seen);
        for (int index = 0; index < readings.size(); index++) {
            if (alreadySaved.contains(readings.get(index).measuredAt()))
                errors.add(detail(index, MEASURED_AT_FIELD, MonitoringMessages.ALREADY_SAVED));
        }

        if (!errors.isEmpty())
            throw new InvalidNoiseReadingsException(MonitoringMessages.INVALID_READINGS, errors);
    }

    private Set<LocalDateTime> findSavedTimes(PatientEntity patient, Set<LocalDateTime> times) {
        var first = Collections.min(times);
        var last = Collections.max(times);
        return new HashSet<>(measurementRepository.findMeasuredAtBetween(patient.getId(), first, last));
    }

    private void raiseCriticalExposure(Long userId, PatientEntity patient, NormalizedReading peak, List<LocalDateTime> criticalTimes) {
        measurementRepository.insertAlerts(patient.getId(), criticalTimes, LocalDateTime.now(clock));
        var alertId = measurementRepository.findAlertId(patient.getId(), peak.measuredAt()).orElseThrow();
        eventPublisher.publishEvent(new CriticalExposureEvent(userId, alertId, peak.dbValue(), criticalTimes.size()));
    }

    private static MeasurementRow toRow(PatientEntity patient, NormalizedReading reading, IntensityLevelEntity level, boolean keepLocation) {
        var location = keepLocation && level.startsAtOrAbove(LOCATION_MIN_DB) ? reading.source().locationType() : null;
        var source = reading.source().source() == null ? MeasurementSource.PHONE : reading.source().source();
        return new MeasurementRow(patient.getId(), level.getId(), location, reading.dbValue(), reading.measuredAt(), source);
    }

    private static IntensityLevelEntity classify(List<IntensityLevelEntity> levels, BigDecimal dbValue) {
        return levels.stream().filter(level -> level.contains(dbValue)).findFirst()
                .orElseThrow(() -> new IllegalStateException(MonitoringMessages.LEVEL_NOT_FOUND + dbValue));
    }

    private NormalizedReading normalize(NoiseReadingDto reading) {
        return new NormalizedReading(reading.dbValue().setScale(DB_SCALE, RoundingMode.HALF_UP), toLocal(reading.measuredAt()), reading);
    }

    private LocalDateTime toLocal(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(clock.getZone()).toLocalDateTime();
    }

    private static ErrorDetail detail(int index, String field, String message) {
        return new ErrorDetail(MonitoringMessages.READING_FIELD.formatted(index, field), message);
    }

    private PatientEntity findPatient(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(MonitoringMessages.PATIENT_NOT_FOUND));
    }

    private record NormalizedReading(BigDecimal dbValue, LocalDateTime measuredAt, NoiseReadingDto source) { }
}
