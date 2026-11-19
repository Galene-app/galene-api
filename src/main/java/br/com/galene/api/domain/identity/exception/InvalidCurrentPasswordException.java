package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends ApiException {
    public InvalidCurrentPasswordException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.INVALID_CURRENT_PASSWORD, message);
    }
}
