package br.com.galene.api.domain.identity.dto.user;

import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.entity.UserEntity;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.enums.ColorblindMode;
import br.com.galene.api.domain.identity.enums.ProfileType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String fullName,
        String email,
        LocalDate birthDate,
        ProfileType profileType,
        AccountStatus accountStatus,
        Boolean darkMode,
        ColorblindMode colorblindMode,
        Boolean ttsEnabled,
        String patientCode,
        LocalDateTime createdAt
) {

    public UserResponseDto(UserEntity user, PatientEntity patient) {
        this(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getBirthDate(),
            user.getProfileType(),
            user.getAccountStatus(),
            user.getDarkMode(),
            user.getColorblindMode(),
            user.getTtsEnabled(),
            patient == null ? null : patient.getPatientCode(),
            user.getCreatedAt()
        );
    }
}
