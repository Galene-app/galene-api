package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Excesso de tentativas, por exemplo no 2FA (429). */
public final class TooManyAttemptsException extends DomainException {

    public TooManyAttemptsException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.TOO_MANY_REQUESTS, code, message);
    }

    public TooManyAttemptsException() {
        this(ErrorCode.TOO_MANY_ATTEMPTS, ErrorMessage.TOO_MANY_ATTEMPTS);
    }
}
