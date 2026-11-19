package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Violação de unicidade ou estado conflitante (409). */
public final class ConflictException extends DomainException {

    public ConflictException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.CONFLICT, code, message);
    }
}
