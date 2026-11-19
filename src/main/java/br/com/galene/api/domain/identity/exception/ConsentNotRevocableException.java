package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class ConsentNotRevocableException extends ApiException {
    public ConsentNotRevocableException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.CONSENT_NOT_REVOCABLE, message);
    }
}
