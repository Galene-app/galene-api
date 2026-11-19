package br.com.galene.api.domain.email.exception;

public class EmailTemplateNotFoundException extends RuntimeException {
    public EmailTemplateNotFoundException(String message) {
        super(message);
    }
}
