package br.com.galene.api.domain.email.service;

import br.com.galene.api.domain.email.constants.EmailMessages;
import br.com.galene.api.domain.email.dto.email.EmailMessageDto;
import br.com.galene.api.domain.email.entity.EmailTemplateEntity;
import br.com.galene.api.domain.email.enums.EmailType;
import br.com.galene.api.domain.email.exception.EmailTemplateNotFoundException;
import br.com.galene.api.domain.email.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;

import static br.com.galene.api.domain.email.constants.EmailPlaceholders.*;
import static br.com.galene.api.domain.email.constants.EmailTemplates.PASSWORD_RESET_VALIDITY_LABEL;
import static br.com.galene.api.domain.email.constants.EmailTemplates.TWO_FACTOR_VALIDITY_LABEL;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public EmailMessageDto twoFactorCode(String toAddress, String toName, String code) {
        var values = Map.of(USER_NAME, toName, CODE, code, VALIDITY, TWO_FACTOR_VALIDITY_LABEL);
        return buildEmail(EmailType.TWO_FACTOR, toAddress, toName, values);
    }

    @Transactional(readOnly = true)
    public EmailMessageDto passwordReset(String toAddress, String toName, String resetLink) {
        var values = Map.of(USER_NAME, toName, RESET_LINK, resetLink, VALIDITY, PASSWORD_RESET_VALIDITY_LABEL);
        return buildEmail(EmailType.PASSWORD_RESET, toAddress, toName, values);
    }

    public EmailTemplateEntity getTemplate(EmailType type) {
        return templateRepository.findByTemplateKey(type)
                .orElseThrow(() -> new EmailTemplateNotFoundException(EmailMessages.TEMPLATE_NOT_FOUND + type));
    }

    private EmailMessageDto buildEmail(EmailType type, String toAddress, String toName, Map<String, String> values) {
        var template = getTemplate(type);
        var html = fill(template.getHtmlBody(), values, HtmlUtils::htmlEscape);
        var plain = template.getPlainBody() == null ? null : fill(template.getPlainBody(), values, UnaryOperator.identity());

        return new EmailMessageDto(toAddress, toName, template.getSubject(), html, plain, type);
    }

    private static String fill(String body, Map<String, String> values, UnaryOperator<String> escape) {
        var matcher = ANY_PLACEHOLDER.matcher(body);
        var filledBody = new StringBuilder();
        while (matcher.find()) {
            var value = values.get(matcher.group());
            if (value == null)
                throw new IllegalStateException(EmailMessages.UNFILLED_PLACEHOLDER + matcher.group());

            matcher.appendReplacement(filledBody, Matcher.quoteReplacement(escape.apply(value)));
        }
        matcher.appendTail(filledBody);
        return filledBody.toString();
    }
}
