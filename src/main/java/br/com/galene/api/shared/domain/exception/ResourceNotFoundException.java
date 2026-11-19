package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Recurso inexistente ou que o usuário não pode saber que existe (404). */
public final class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }

    public ResourceNotFoundException() {
        this(ErrorCode.RESOURCE_NOT_FOUND, ErrorMessage.RESOURCE_NOT_FOUND);
    }
}
