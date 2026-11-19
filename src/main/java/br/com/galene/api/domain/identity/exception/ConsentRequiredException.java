package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class ConsentRequiredException extends ApiException {
    public ConsentRequiredException(String message) {
        super(HttpStatus.FORBIDDEN, ErrorCodes.CONSENT_REQUIRED, message);
    }
}
