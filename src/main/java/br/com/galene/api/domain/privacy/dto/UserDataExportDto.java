package br.com.galene.api.domain.privacy.dto;

import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.monitoring.dto.NoiseReadingResponseDto;
import br.com.galene.api.domain.notification.dto.NotificationDto;
import br.com.galene.api.domain.questionnaire.dto.SoundProfileDto;

import java.time.OffsetDateTime;
import java.util.List;

public record UserDataExportDto(
        OffsetDateTime exportedAt,
        UserResponseDto profile,
        PatientExportDto patient,
        ConsentsExportDto consents,
        List<AnswerExportDto> questionnaireAnswers,
        SoundProfileDto soundProfile,
        List<NoiseReadingResponseDto> noiseReadings,
        List<NotificationDto> notifications
) { }
