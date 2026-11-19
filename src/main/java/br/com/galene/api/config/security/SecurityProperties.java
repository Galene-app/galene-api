package br.com.galene.api.config.security;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "galene.security")
public class SecurityProperties {

    public static final int MIN_TOKEN_SECRET_BYTES = 32;

    @NotBlank
    private String tokenSecret;

    @NotNull
    private Duration accessTokenTtl;

    @NotNull
    private Duration refreshTokenTtl;

    private boolean hasMfa = true;

    private List<String> corsAllowedOrigins = new ArrayList<>();

    @AssertTrue(message = "Minimo de 32 bytes")
    public boolean isTokenSecretLongEnough() {
        return tokenSecret != null && tokenSecret.getBytes(StandardCharsets.UTF_8).length >= MIN_TOKEN_SECRET_BYTES;
    }
}
