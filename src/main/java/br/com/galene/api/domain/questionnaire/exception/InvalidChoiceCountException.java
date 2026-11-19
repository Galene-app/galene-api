package br.com.galene.api.domain.questionnaire.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidChoiceCountException extends ApiException {
    public InvalidChoiceCountException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.INVALID_CHOICE_COUNT, message);
    }
}