package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Regra de negócio violada com dados sintaticamente válidos (422). */
public final class BusinessRuleException extends DomainException {

    public BusinessRuleException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, code, message);
    }
}
