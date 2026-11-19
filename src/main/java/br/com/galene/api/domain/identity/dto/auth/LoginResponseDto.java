package br.com.galene.api.domain.identity.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDto(
        Boolean mfaRequired,
        Instant codeExpiresAt,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Boolean passwordChangeRequired
) {

    public static LoginResponseDto codeSent(Instant codeExpiresAt) {
        return new LoginResponseDto(true, codeExpiresAt, null, null, null, null);
    }

    public static LoginResponseDto tokens(AccessTokenDto accessToken, String refreshToken, boolean passwordChangeRequired) {
        return new LoginResponseDto(false, null, accessToken.token(), refreshToken, accessToken.expiresIn(), passwordChangeRequired);
    }

    @Override
    public String toString() {
        return "LoginResponseDto[mfaRequired=" + mfaRequired + ", passwordChangeRequired=" + passwordChangeRequired + "]";
    }
}
