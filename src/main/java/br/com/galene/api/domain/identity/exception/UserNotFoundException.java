package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ErrorCodes.USER_NOT_FOUND, message);
    }
}