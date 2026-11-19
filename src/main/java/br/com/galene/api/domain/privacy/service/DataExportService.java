package br.com.galene.api.domain.privacy.service;

import br.com.galene.api.domain.identity.dto.consent.ConsentResponseDto;
import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.repository.ConsentRepository;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.service.ConsentService;
import br.com.galene.api.domain.identity.service.UserService;
import br.com.galene.api.domain.monitoring.dto.NoiseReadingResponseDto;
import br.com.galene.api.domain.monitoring.repository.MeasurementRepository;
import br.com.galene.api.domain.notification.dto.NotificationDto;
import br.com.galene.api.domain.notification.repository.NotificationRepository;
import br.com.galene.api.domain.privacy.dto.AnswerExportDto;
import br.com.galene.api.domain.privacy.dto.ConsentsExportDto;
import br.com.galene.api.domain.privacy.dto.PatientExportDto;
import br.com.galene.api.domain.privacy.dto.UserDataExportDto;
import br.com.galene.api.domain.questionnaire.entity.AnswerEntity;
import br.com.galene.api.domain.questionnaire.entity.QuestionOptionEntity;
import br.com.galene.api.domain.questionnaire.repository.AnswerRepository;
import br.com.galene.api.domain.questionnaire.service.SoundProfileCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DataExportService {

    private final UserService userService;
    private final PatientRepository patientRepository;
    private final ConsentRepository consentRepository;
    private final AnswerRepository answerRepository;
    private final MeasurementRepository measurementRepository;
    private final NotificationRepository notificationRepository;
    private final SoundProfileCalculator profileCalculator;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserDataExportDto exportUserData(Long userId) {
        var user = userService.getUserById(userId);
        var patient = patientRepository.findByUserId(userId).orElse(null);
        var answers = patient == null ? List.<AnswerEntity>of() : answerRepository.findByPatientId(patient.getId());

        var export = new UserDataExportDto(
                OffsetDateTime.now(clock),
                new UserResponseDto(user, patient),
                patient == null ? null : new PatientExportDto(patient),
                exportConsents(userId),
                toAnswerExport(answers),
                answers.isEmpty() ? null : profileCalculator.calculate(answers.stream().map(answer -> answer.getOption().getOptionCode()).toList()),
                exportReadings(patient),
                notificationRepository.findByUserIdOrderByCreatedAtAscIdAsc(userId).stream()
                        .map(notification -> new NotificationDto(notification, clock.getZone())).toList());

        log.info("Usuário {} exportou os próprios dados", userId);
        return export;
    }

    private ConsentsExportDto exportConsents(Long userId) {
        var newestFirst = consentRepository.findByUserIdOrderByAnsweredAtDescIdDesc(userId);
        var current = ConsentService.latestPerTerm(newestFirst).values().stream().map(ConsentResponseDto::new).toList();
        var history = newestFirst.reversed().stream().map(ConsentResponseDto::new).toList();
        return new ConsentsExportDto(current, history);
    }

    private List<NoiseReadingResponseDto> exportReadings(PatientEntity patient) {
        if (patient == null) return List.of();

        return measurementRepository.findByPatientIdOrderByMeasuredAtAsc(patient.getId()).stream()
                .map(measurement -> new NoiseReadingResponseDto(measurement, clock.getZone())).toList();
    }

    private List<AnswerExportDto> toAnswerExport(List<AnswerEntity> answers) {
        var byQuestion = answers.stream().collect(Collectors.groupingBy(answer -> answer.getQuestion().getDisplayOrder(), TreeMap::new, Collectors.toList()));

        return byQuestion.values().stream().map(questionAnswers -> new AnswerExportDto(
                questionAnswers.getFirst().getQuestion().getQuestionText(),
                questionAnswers.stream().map(AnswerEntity::getOption)
                        .sorted(Comparator.comparing(QuestionOptionEntity::getDisplayOrder))
                        .map(QuestionOptionEntity::getOptionText).toList(),
                questionAnswers.getFirst().getAnsweredAt().atZone(clock.getZone()).toOffsetDateTime()
        )).toList();
    }
}
