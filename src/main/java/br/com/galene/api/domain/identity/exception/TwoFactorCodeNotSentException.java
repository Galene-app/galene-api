package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class TwoFactorCodeNotSentException extends ApiException {
    public TwoFactorCodeNotSentException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.TWO_FACTOR_CODE_NOT_SENT, message);
    }
}
