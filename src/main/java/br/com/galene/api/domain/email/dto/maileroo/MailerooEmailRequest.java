package br.com.galene.api.domain.email.dto.maileroo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailerooEmailRequest(
        MailerooEmailAddress from,
        List<MailerooEmailAddress> to,
        String subject,
        String html,
        String plain,
        Boolean tracking,
        Map<String, String> tags
) { }
