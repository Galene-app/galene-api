package br.com.galene.api.domain.reporting.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class TestModeDisabledException extends ApiException {
    public TestModeDisabledException(String message) {
        super(HttpStatus.FORBIDDEN, ErrorCodes.TEST_MODE_DISABLED, message);
    }
}
