package br.com.galene.api.domain.identity.service;

import br.com.galene.api.common.security.SecureCodeGenerator;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.dto.user.UpdateUserDto;
import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.identity.entity.ConsentEntity;
import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.entity.TermEntity;
import br.com.galene.api.domain.identity.entity.UserEntity;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.exception.UserNotFoundException;
import br.com.galene.api.domain.identity.repository.ConsentRepository;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.repository.TermRepository;
import br.com.galene.api.domain.identity.repository.UserRepository;
import br.com.galene.api.domain.identity.validation.register.RegisterValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    public static final String PATIENT_CODE_FORMAT = "GLN-%05d";
    public static final String DELETED_USER_NAME = "Conta excluída";
    public static final String DELETED_EMAIL_FORMAT = "deleted-%d@galene.invalid";
    public static final LocalDate DELETED_BIRTH_DATE = LocalDate.of(1900, 1, 1);
    private static final int DELETED_PASSWORD_RANDOM_BYTES = 32;

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ConsentRepository consentRepository;
    private final TermRepository termRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureCodeGenerator secureCodeGenerator;
    private final List<RegisterValidate> registerValidators;
    private final SessionService sessionService;
    private final Clock clock;

    @Transactional
    public UserResponseDto registerUser(RegisterRequestDto request) {
        registerValidators.forEach(validator -> validator.validate(request));
        var now = LocalDateTime.now(clock);

        var newUser = userRepository.save(new UserEntity(request, passwordEncoder.encode(request.password()), now));
        var patient = patientRepository.save(new PatientEntity(newUser, nextPatientCode(), now.toLocalDate()));
        consentRepository.save(new ConsentEntity(newUser, null, getCurrentTerm(TermType.ACCOUNT), true, now));

        log.info("Usuário {} cadastrado com o paciente {}", newUser.getId(), patient.getId());
        return new UserResponseDto(newUser, patient);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserDetails(Long userId) {
        var user = getUserById(userId);
        return new UserResponseDto(user, findPatient(userId));
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UpdateUserDto request) {
        var userDb = getUserById(userId);
        userDb.setFullName(request.fullName().trim());
        userDb.setDarkMode(request.darkMode());
        userDb.setColorblindMode(request.colorblindMode());
        userDb.setTtsEnabled(request.ttsEnabled());

        log.info("Usuário {} atualizou o perfil", userId);
        return new UserResponseDto(userDb, findPatient(userId));
    }

    @Transactional
    public void deleteUser(Long userId) {
        var userToDelete = getUserById(userId);
        userToDelete.setFullName(DELETED_USER_NAME);
        userToDelete.setEmail(DELETED_EMAIL_FORMAT.formatted(userId));
        userToDelete.setPasswordHash(passwordEncoder.encode(secureCodeGenerator.opaqueToken(DELETED_PASSWORD_RANDOM_BYTES)));
        userToDelete.setPhone(null);
        userToDelete.setPhotoUrl(null);
        userToDelete.setBirthDate(DELETED_BIRTH_DATE);
        userToDelete.setAccountStatus(AccountStatus.INACTIVE);

        var patient = findPatient(userId);
        if (patient != null) patient.setDiagnosis(null);

        sessionService.revokeAllSessions(userId);
        log.info("Usuário {} excluído (dados anonimizados)", userId);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findByIdAndAccountStatusNot(id, AccountStatus.INACTIVE)
                .orElseThrow(() -> new UserNotFoundException(UserMessages.USER_NOT_FOUND));
    }

    private PatientEntity findPatient(Long userId) {
        return patientRepository.findByUserId(userId).orElse(null);
    }

    private String nextPatientCode() {
        return PATIENT_CODE_FORMAT.formatted(patientRepository.findLastPatientCodeNumber() + 1);
    }

    private TermEntity getCurrentTerm(TermType termType) {
        return termRepository.findFirstByTermTypeOrderByPublishedAtDesc(termType)
                .orElseThrow(() -> new IllegalStateException(UserMessages.NO_TERM_PUBLISHED));
    }
}
