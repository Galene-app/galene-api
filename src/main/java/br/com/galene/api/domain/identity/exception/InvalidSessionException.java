package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidSessionException extends ApiException {
    public InvalidSessionException(String message) {
        super(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_SESSION, message);
    }
}