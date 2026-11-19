package br.com.galene.api.domain.email.service;

import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.domain.email.configuration.MailerooConfig;
import br.com.galene.api.domain.email.constants.EmailMessages;
import br.com.galene.api.domain.email.dto.email.EmailMessageDto;
import br.com.galene.api.domain.email.dto.maileroo.MailerooEmailAddress;
import br.com.galene.api.domain.email.dto.maileroo.MailerooEmailRequest;
import br.com.galene.api.domain.email.dto.maileroo.MailerooResponseDto;
import br.com.galene.api.domain.email.exception.EmailFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailerooService {

    public static final String EMAILS_PATH = "/emails";
    public static final String TAG_TYPE = "type";
    private static final boolean TRACKING_ENABLED = false;

    private final RestClient mailerooRestClient;
    private final MailerooConfig config;

    public void sendEmail(EmailMessageDto email) {
        try {
            mailerooRestClient.post()
                    .uri(EMAILS_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createBody(email))
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) return null;
                        throw failure(email, response.getStatusCode(), readMessage(response));
                    });
        } catch (RestClientException ex) {
            log.error("Falha na comunicação com o Maileroo ao enviar e-mail {} para {}", email.type(), EmailHelper.mask(email.toAddress()), ex);
            throw new EmailFailedException(EmailMessages.COMMUNICATION_FAILED, ex);
        }
        log.info("E-mail {} enviado para {}", email.type(), EmailHelper.mask(email.toAddress()));
    }

    private MailerooEmailRequest createBody(EmailMessageDto email) {
        var from = new MailerooEmailAddress(config.getFromAddress(), config.getFromName());
        var to = new MailerooEmailAddress(email.toAddress(), email.toName());

        return new MailerooEmailRequest(
                from,
                List.of(to),
                email.subject(),
                email.html(),
                email.plain(),
                TRACKING_ENABLED,
                Map.of(TAG_TYPE, email.type().getTag())
        );
    }

    private EmailFailedException failure(EmailMessageDto email, HttpStatusCode status, String mailerooMessage) {
        if (status.value() == HttpStatus.UNAUTHORIZED.value()) log.error(EmailMessages.INVALID_API_KEY);

        log.error("Falha ao enviar e-mail {} para {}: HTTP {} - {}", email.type(), EmailHelper.mask(email.toAddress()), status.value(), mailerooMessage);
        return new EmailFailedException(EmailMessages.SEND_FAILED);
    }

    private static String readMessage(ConvertibleClientHttpResponse response) {
        try {
            var body = response.bodyTo(MailerooResponseDto.class);
            return body == null ? null : body.message();
        } catch (Exception ex) {
            return null;
        }
    }
}
