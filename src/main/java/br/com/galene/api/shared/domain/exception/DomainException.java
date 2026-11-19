package br.com.galene.api.shared.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Raiz das exceções de regra de negócio. Cada subclasse fixa o status HTTP;
 * o {@link ErrorCode} e a {@link ErrorMessage} são informados por quem lança.
 */
@Getter
public abstract sealed class DomainException extends RuntimeException
        permits ResourceNotFoundException, BusinessRuleException, ConflictException,
        AccessDeniedDomainException, AuthenticationFailedException, TooManyAttemptsException {

    private final ErrorCode code;
    private final HttpStatus status;

    protected DomainException(HttpStatus status, ErrorCode code, ErrorMessage message) {
        super(message.text());
        this.status = status;
        this.code = code;
    }
}
