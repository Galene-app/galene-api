package br.com.galene.api.domain.reporting.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class InsufficientDataException extends ApiException {
    public InsufficientDataException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.INSUFFICIENT_DATA, message);
    }
}
