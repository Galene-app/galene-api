package br.com.galene.api.domain.monitoring.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class TooManyReadingsException extends ApiException {
    public TooManyReadingsException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.TOO_MANY_READINGS, message);
    }
}
