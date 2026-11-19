package br.com.galene.api.domain.questionnaire.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class OptionNotInQuestionException extends ApiException {
    public OptionNotInQuestionException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.OPTION_NOT_IN_QUESTION, message);
    }
}