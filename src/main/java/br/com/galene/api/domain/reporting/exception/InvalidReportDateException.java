package br.com.galene.api.domain.reporting.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidReportDateException extends ApiException {
    public InvalidReportDateException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.INVALID_REPORT_DATE, message);
    }
}
