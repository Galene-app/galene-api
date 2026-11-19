package br.com.galene.api.domain.identity.service;

import br.com.galene.api.config.security.SecurityProperties;
import br.com.galene.api.domain.identity.dto.auth.AccessTokenDto;
import br.com.galene.api.domain.identity.enums.ProfileType;
import br.com.galene.api.domain.identity.security.UserSecurity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;

@Service
public class TokenService {

    public static final String ISSUER = "galene-api";
    public static final String ROLE_CLAIM = "role";
    public static final String SESSION_CLAIM = "sid";
    private static final String INVALID_CLAIMS_MESSAGE = "Token claims não identifica um usuário válido";

    private final SecurityProperties properties;
    private final Clock clock;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public TokenService(SecurityProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        this.algorithm = Algorithm.HMAC256(properties.getTokenSecret().getBytes(StandardCharsets.UTF_8));
        var verification = (JWTVerifier.BaseVerification) JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withClaimPresence(ROLE_CLAIM)
                .withClaimPresence(SESSION_CLAIM);
        this.verifier = verification.build(clock);
    }

    public AccessTokenDto generateToken(Long userId, ProfileType role, Long sessionId) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(properties.getAccessTokenTtl());
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))
                .withClaim(ROLE_CLAIM, role.name())
                .withClaim(SESSION_CLAIM, sessionId)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
        return new AccessTokenDto(token, expiresAt, properties.getAccessTokenTtl().toSeconds());
    }

    public UserSecurity getSubject(String tokenJwt) throws JWTVerificationException {
        var decoded = verifier.verify(tokenJwt);
        String subject = decoded.getSubject();
        String role = decoded.getClaim(ROLE_CLAIM).asString();
        Long sessionId = decoded.getClaim(SESSION_CLAIM).asLong();
        if (subject == null || role == null || sessionId == null) throw invalidClaims(null);

        try {
            return new UserSecurity(Long.valueOf(subject), ProfileType.valueOf(role), sessionId);
        } catch (IllegalArgumentException ex) {
            throw invalidClaims(ex);
        }
    }

    private static JWTVerificationException invalidClaims(Throwable cause) {
        return new JWTVerificationException(INVALID_CLAIMS_MESSAGE, cause);
    }
}
