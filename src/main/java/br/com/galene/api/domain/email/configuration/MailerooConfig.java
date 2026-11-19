package br.com.galene.api.domain.email.configuration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@Validated
@ConfigurationProperties(prefix = "galene.mail.maileroo")
@Getter
@Setter
public class MailerooConfig {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String apiKey;

    @NotBlank
    @Email
    private String fromAddress;

    @NotBlank
    private String fromName;

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofSeconds(10);
}
