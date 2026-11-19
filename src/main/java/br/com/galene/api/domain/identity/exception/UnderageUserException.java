package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class UnderageUserException extends ApiException {
    public UnderageUserException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.UNDERAGE, message);
    }
}