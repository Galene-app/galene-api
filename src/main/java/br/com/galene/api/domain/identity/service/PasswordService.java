package br.com.galene.api.domain.identity.service;

import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.common.security.SecureCodeGenerator;
import br.com.galene.api.domain.email.dto.event.EmailRequestedEvent;
import br.com.galene.api.domain.email.service.EmailTemplateService;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.auth.ForgotPasswordRequestDto;
import br.com.galene.api.domain.identity.dto.user.ChangePasswordRequestDto;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.exception.InvalidCurrentPasswordException;
import br.com.galene.api.domain.identity.exception.SamePasswordException;
import br.com.galene.api.domain.identity.exception.UserNotFoundException;
import br.com.galene.api.domain.identity.exception.WeakPasswordException;
import br.com.galene.api.domain.identity.repository.UserRepository;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.validation.register.PasswordStrengthValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PasswordService {

    public static final int TEMPORARY_PASSWORD_LENGTH = 12;
    private static final String NAME_SEPARATOR = " ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureCodeGenerator secureCodeGenerator;
    private final EmailTemplateService emailTemplateService;
    private final SessionService sessionService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendTemporaryPassword(ForgotPasswordRequestDto request) {
        var temporaryPassword = secureCodeGenerator.temporaryPassword(TEMPORARY_PASSWORD_LENGTH);
        var temporaryPasswordHash = passwordEncoder.encode(temporaryPassword);

        var user = userRepository.findByEmailAndAccountStatusNot(EmailHelper.normalize(request.email()), AccountStatus.INACTIVE);
        if (user.isEmpty()) return;

        var userDb = user.get();
        userDb.setPasswordHash(temporaryPasswordHash);
        userDb.setPasswordChangeRequired(true);
        sessionService.revokeAllSessions(userDb.getId());

        var firstName = userDb.getFullName().split(NAME_SEPARATOR)[0];
        eventPublisher.publishEvent(new EmailRequestedEvent(emailTemplateService.temporaryPassword(userDb.getEmail(), firstName, temporaryPassword)));
        log.info("Senha temporária gerada para o usuário {}", userDb.getId());
    }

    @Transactional
    public void changePassword(UserSecurity user, ChangePasswordRequestDto request) {
        var userDb = userRepository.findByIdAndAccountStatusNot(user.userId(), AccountStatus.INACTIVE)
                .orElseThrow(() -> new UserNotFoundException(UserMessages.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), userDb.getPasswordHash()))
            throw new InvalidCurrentPasswordException(UserMessages.INVALID_CURRENT_PASSWORD);
        if (!PasswordStrengthValidate.isStrong(request.newPassword()))
            throw new WeakPasswordException(UserMessages.WEAK_PASSWORD);
        if (request.newPassword().equals(request.currentPassword()))
            throw new SamePasswordException(UserMessages.SAME_PASSWORD);

        userDb.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userDb.setPasswordChangeRequired(false);
        sessionService.revokeOtherSessions(user.userId(), user.sessionId());
        log.info("Usuário {} alterou a senha", user.userId());
    }
}
