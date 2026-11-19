package br.com.galene.api.domain.questionnaire.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class UnansweredQuestionException extends ApiException {
    public UnansweredQuestionException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.QUESTION_NOT_ANSWERED, message);
    }
}