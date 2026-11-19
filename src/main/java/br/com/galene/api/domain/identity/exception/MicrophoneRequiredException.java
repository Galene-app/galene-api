package br.com.galene.api.domain.identity.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class MicrophoneRequiredException extends ApiException {
    public MicrophoneRequiredException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.MICROPHONE_REQUIRED, message);
    }
}
