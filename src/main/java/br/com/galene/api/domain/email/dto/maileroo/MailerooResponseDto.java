package br.com.galene.api.domain.email.dto.maileroo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MailerooResponseDto(Boolean success, String message, Data data) {

    public record Data(@JsonProperty("reference_id") String referenceId) { }
}
