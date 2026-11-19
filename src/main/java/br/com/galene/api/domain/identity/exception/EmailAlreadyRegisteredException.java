package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends ApiException {
    public EmailAlreadyRegisteredException(String message) {
        super(HttpStatus.CONFLICT, ErrorCodes.EMAIL_ALREADY_REGISTERED, message);
    }
}