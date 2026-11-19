package br.com.galene.api.domain.monitoring.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import br.com.galene.api.common.model.ErrorResponse.ErrorDetail;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidNoiseReadingsException extends ApiException {
    public InvalidNoiseReadingsException(String message, List<ErrorDetail> details) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.INVALID_NOISE_READINGS, message, details);
    }
}
