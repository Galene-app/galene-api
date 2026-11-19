package br.com.galene.api.domain.identity.service;

import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.common.helpers.HashHelper;
import br.com.galene.api.common.security.SecureCodeGenerator;
import br.com.galene.api.config.security.SecurityProperties;
import br.com.galene.api.domain.email.constants.EmailTemplates;
import br.com.galene.api.domain.email.exception.EmailFailedException;
import br.com.galene.api.domain.email.service.EmailTemplateService;
import br.com.galene.api.domain.email.service.MailerooService;
import br.com.galene.api.domain.identity.constants.AuthMessages;
import br.com.galene.api.domain.identity.dto.auth.LoginRequestDto;
import br.com.galene.api.domain.identity.dto.auth.LoginResponseDto;
import br.com.galene.api.domain.identity.dto.auth.RefreshTokenRequestDto;
import br.com.galene.api.domain.identity.dto.auth.TwoFactorVerifyRequestDto;
import br.com.galene.api.domain.identity.entity.UserEntity;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.exception.InvalidCredentialsException;
import br.com.galene.api.domain.identity.exception.InvalidTwoFactorCodeException;
import br.com.galene.api.domain.identity.exception.TwoFactorCodeNotSentException;
import br.com.galene.api.domain.identity.repository.UserRepository;
import br.com.galene.api.domain.identity.security.UserSecurity;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Log4j2
@Service
public class AuthService {

    public static final int CODE_DIGITS = 6;
    public static final Duration CODE_VALIDITY = Duration.ofMinutes(EmailTemplates.TWO_FACTOR_VALIDITY_MINUTES);
    private static final String DUMMY_PASSWORD = "Dummy@Password#For-Timing-Equalization";
    private static final String NAME_SEPARATOR = " ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final EmailTemplateService emailTemplateService;
    private final SecureCodeGenerator secureCodeGenerator;
    private final SecurityProperties securityProperties;
    private final MailerooService mailerooService;
    private final Clock clock;
    private final String dummyPasswordHash;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, SessionService sessionService,
                       EmailTemplateService emailTemplateService, SecureCodeGenerator secureCodeGenerator,
                       SecurityProperties securityProperties, MailerooService mailerooService, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.emailTemplateService = emailTemplateService;
        this.secureCodeGenerator = secureCodeGenerator;
        this.securityProperties = securityProperties;
        this.mailerooService = mailerooService;
        this.clock = clock;
        this.dummyPasswordHash = passwordEncoder.encode(DUMMY_PASSWORD);
    }

    @Transactional
    public LoginResponseDto authenticate(LoginRequestDto request) {
        var user = validateCredentials(request);
        if (!securityProperties.isHasMfa()) return sessionService.createSession(user);

        var code = secureCodeGenerator.numericCode(CODE_DIGITS);
        sendCode(user, code);

        user.setMfaCodeHash(hashMfaCode(code));
        user.setMfaExpiresAt(LocalDateTime.now(clock).plus(CODE_VALIDITY));
        log.info("Código 2FA enviado para o usuário {}", user.getId());
        return LoginResponseDto.codeSent(user.getMfaExpiresAt().atZone(clock.getZone()).toInstant());
    }

    // O código vale uma única tentativa: certo ou errado, ele é apagado. Errou, faz login de novo.
    @Transactional(noRollbackFor = InvalidTwoFactorCodeException.class)
    public LoginResponseDto verifyTwoFactorCode(TwoFactorVerifyRequestDto request) {
        var user = userRepository.findByEmailAndAccountStatusNot(EmailHelper.normalize(request.email()), AccountStatus.INACTIVE)
                .orElseThrow(() -> new InvalidTwoFactorCodeException(AuthMessages.INVALID_CODE));

        var codeIsValid = user.getMfaCodeHash() != null
                && !LocalDateTime.now(clock).isAfter(user.getMfaExpiresAt())
                && HashHelper.matches(user.getMfaCodeHash(), hashMfaCode(request.code()));

        user.setMfaCodeHash(null);
        user.setMfaExpiresAt(null);

        if (!codeIsValid) {
            log.warn("Código 2FA inválido para o usuário {}", user.getId());
            throw new InvalidTwoFactorCodeException(AuthMessages.INVALID_CODE);
        }
        return sessionService.createSession(user);
    }

    public LoginResponseDto refresh(RefreshTokenRequestDto request) {
        return sessionService.refresh(request.refreshToken());
    }

    public void logout(UserSecurity user) {
        sessionService.logout(user.userId(), user.sessionId());
    }

    // Senha errada, e-mail inexistente e conta inativa recebem a mesma resposta; o BCrypt roda
    // nos três casos para que o tempo de resposta não revele se o e-mail está cadastrado
    private UserEntity validateCredentials(LoginRequestDto request) {
        var user = userRepository.findByEmailAndAccountStatusNot(EmailHelper.normalize(request.email()), AccountStatus.INACTIVE);
        var passwordMatches = passwordEncoder.matches(request.password(), user.map(UserEntity::getPasswordHash).orElse(dummyPasswordHash));
        if (user.isEmpty() || !passwordMatches)
            throw new InvalidCredentialsException(AuthMessages.INVALID_CREDENTIALS);

        return user.get();
    }

    private void sendCode(UserEntity user, String code) {
        var firstName = user.getFullName().split(NAME_SEPARATOR)[0];
        try {
            mailerooService.sendEmail(emailTemplateService.twoFactorCode(user.getEmail(), firstName, code));
        } catch (EmailFailedException ex) {
            log.error("Login do usuário {} interrompido: código 2FA não enviado", user.getId());
            throw new TwoFactorCodeNotSentException(AuthMessages.CODE_NOT_SENT);
        }
    }

    private String hashMfaCode(String code) {
        return HashHelper.hmacSha256Hex(securityProperties.getTokenSecret(), code);
    }
}
