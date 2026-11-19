package br.com.galene.api.domain.reporting.service;

import br.com.galene.api.domain.billing.exception.PremiumRequiredException;
import br.com.galene.api.domain.billing.service.SubscriptionService;
import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.service.ConsentService;
import br.com.galene.api.domain.monitoring.repository.IntensityLevelRepository;
import br.com.galene.api.domain.monitoring.repository.MeasurementRepository;
import br.com.galene.api.domain.reporting.configuration.ReportTestMode;
import br.com.galene.api.domain.reporting.constants.ReportMessages;
import br.com.galene.api.domain.reporting.dto.DailyReportDto;
import br.com.galene.api.domain.reporting.dto.event.DailyReportGeneratedEvent;
import br.com.galene.api.domain.reporting.entity.ReportEntity;
import br.com.galene.api.domain.reporting.enums.ReportType;
import br.com.galene.api.domain.reporting.exception.InsufficientDataException;
import br.com.galene.api.domain.reporting.exception.InvalidReportDateException;
import br.com.galene.api.domain.reporting.exception.TestModeDisabledException;
import br.com.galene.api.domain.reporting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    public static final Duration MINIMUM_USAGE = Duration.ofHours(24);

    private final MeasurementRepository measurementRepository;
    private final IntensityLevelRepository levelRepository;
    private final ReportRepository reportRepository;
    private final PatientRepository patientRepository;
    private final ConsentService consentService;
    private final SubscriptionService subscriptionService;
    private final DailyReportCalculator calculator;
    private final ReportTestMode reportTestMode;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public DailyReportDto getDailyReport(Long userId, LocalDate requestedDate, boolean testMode) {
        if (testMode && !reportTestMode.isEnabled())
            throw new TestModeDisabledException(ReportMessages.TEST_MODE_DISABLED);

        consentService.requireConsent(userId, TermType.DATA_USE);
        var today = LocalDate.now(clock);
        var yesterday = today.minusDays(1);
        var date = requestedDate == null ? yesterday : requestedDate;

        if (testMode) validateTestModeDate(date, today);
        else validateDate(userId, date, today, yesterday);

        var patient = findPatient(userId);
        if (!testMode && !hasMinimumUsage(patient.getId()))
            throw new InsufficientDataException(ReportMessages.NOT_ENOUGH_USAGE);

        if (testMode) log.warn("Relatório de {} emitido em modo de teste para o usuário {}", date, userId);
        return buildDailyReport(patient.getId(), date)
                .orElseThrow(() -> new InsufficientDataException(ReportMessages.NO_READINGS));
    }

    private void validateDate(Long userId, LocalDate date, LocalDate today, LocalDate yesterday) {
        if (!date.isBefore(today))
            throw new InvalidReportDateException(ReportMessages.INVALID_DATE);
        if (!date.equals(yesterday) && !subscriptionService.isPremium(userId))
            throw new PremiumRequiredException(ReportMessages.PREMIUM_DAY);
    }

    private static void validateTestModeDate(LocalDate date, LocalDate today) {
        if (date.isAfter(today))
            throw new InvalidReportDateException(ReportMessages.FUTURE_DATE);
    }

    public void requirePremiumPeriod() {
        throw new PremiumRequiredException(ReportMessages.PREMIUM_PERIOD);
    }

    @Transactional(readOnly = true)
    public List<Long> findPatientsWithReadings(LocalDate date) {
        return measurementRepository.findPatientsWithReadingsBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    @Transactional
    public boolean generateDailyReport(Long patientId, LocalDate date) {
        if (reportRepository.existsByPatientIdAndReportTypeAndPeriodStart(patientId, ReportType.DAILY, date)) return false;
        if (!hasMinimumUsage(patientId)) return false;
        if (!measurementRepository.existsByPatientIdAndMeasuredAtGreaterThanEqualAndMeasuredAtLessThan(
                patientId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())) return false;

        var userId = patientRepository.findById(patientId).orElseThrow().getUser().getId();
        reportRepository.save(ReportEntity.daily(patientId, userId, date, LocalDateTime.now(clock)));
        eventPublisher.publishEvent(new DailyReportGeneratedEvent(userId, date));
        log.info("Relatório diário de {} gerado para o usuário {}", date, userId);
        return true;
    }

    private Optional<DailyReportDto> buildDailyReport(Long patientId, LocalDate date) {
        var readings = measurementRepository.findReadingPoints(patientId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        if (readings.isEmpty()) return Optional.empty();

        return Optional.of(calculator.calculate(date, readings, levelRepository.findAllByOrderByDbMinAsc(), clock.getZone()));
    }

    private boolean hasMinimumUsage(Long patientId) {
        var latestFirstReading = LocalDateTime.now(clock).minus(MINIMUM_USAGE);
        return measurementRepository.findFirstReadingTime(patientId)
                .map(firstReading -> !firstReading.isAfter(latestFirstReading))
                .orElse(false);
    }

    private PatientEntity findPatient(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(ReportMessages.PATIENT_NOT_FOUND));
    }
}
