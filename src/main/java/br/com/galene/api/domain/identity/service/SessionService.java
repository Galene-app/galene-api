package br.com.galene.api.domain.identity.service;

import br.com.galene.api.common.helpers.HashHelper;
import br.com.galene.api.common.security.SecureCodeGenerator;
import br.com.galene.api.config.security.SecurityProperties;
import br.com.galene.api.domain.identity.constants.AuthMessages;
import br.com.galene.api.domain.identity.dto.auth.LoginResponseDto;
import br.com.galene.api.domain.identity.entity.UserEntity;
import br.com.galene.api.domain.identity.entity.UserSessionEntity;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.exception.InvalidSessionException;
import br.com.galene.api.domain.identity.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessionService {

    public static final int REFRESH_TOKEN_BYTES = 32;

    private final UserSessionRepository sessionRepository;
    private final TokenService tokenService;
    private final SecureCodeGenerator secureCodeGenerator;
    private final SecurityProperties securityProperties;
    private final Clock clock;

    @Transactional
    public LoginResponseDto createSession(UserEntity user) {
        var now = LocalDateTime.now(clock);
        var refreshToken = secureCodeGenerator.opaqueToken(REFRESH_TOKEN_BYTES);
        var session = sessionRepository.save(new UserSessionEntity(user, HashHelper.sha256Hex(refreshToken), now));

        log.info("Sessão {} criada para o usuário {}", session.getId(), user.getId());
        return LoginResponseDto.tokens(tokenService.generateToken(user.getId(), user.getProfileType(), session.getId()), refreshToken);
    }

    @Transactional
    public LoginResponseDto refresh(String refreshToken) {
        var now = LocalDateTime.now(clock);
        var session = sessionRepository.findByRefreshTokenHash(HashHelper.sha256Hex(refreshToken))
                .filter(found -> isActive(found, now))
                .orElseThrow(() -> new InvalidSessionException(AuthMessages.INVALID_SESSION));

        var newRefreshToken = secureCodeGenerator.opaqueToken(REFRESH_TOKEN_BYTES);
        session.setRefreshTokenHash(HashHelper.sha256Hex(newRefreshToken));
        session.setRefreshedAt(now);

        var user = session.getUser();
        return LoginResponseDto.tokens(tokenService.generateToken(user.getId(), user.getProfileType(), session.getId()), newRefreshToken);
    }

    @Transactional
    public void logout(Long userId, Long sessionId) {
        var now = LocalDateTime.now(clock);
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .filter(session -> session.getRevokedAt() == null)
                .ifPresent(session -> session.setRevokedAt(now));

        log.info("Sessão {} encerrada pelo usuário {}", sessionId, userId);
    }

    @Transactional
    public void revokeAllSessions(Long userId) {
        int revoked = sessionRepository.revokeAllActiveSessions(userId, LocalDateTime.now(clock));
        log.info("{} sessão(ões) revogada(s) do usuário {}", revoked, userId);
    }

    private boolean isActive(UserSessionEntity session, LocalDateTime now) {
        if (session.getRevokedAt() != null) return false;
        if (session.getUser().getAccountStatus() == AccountStatus.INACTIVE) return false;

        var expiresAt = session.getRefreshedAt().plus(securityProperties.getRefreshTokenTtl());
        return !now.isAfter(expiresAt);
    }
}
