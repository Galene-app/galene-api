package br.com.galene.api.domain.billing.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class PremiumRequiredException extends ApiException {
    public PremiumRequiredException(String message) {
        super(HttpStatus.FORBIDDEN, ErrorCodes.PREMIUM_REQUIRED, message);
    }
}
