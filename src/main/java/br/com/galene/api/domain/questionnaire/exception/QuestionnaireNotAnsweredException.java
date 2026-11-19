package br.com.galene.api.domain.questionnaire.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class QuestionnaireNotAnsweredException extends ApiException {
    public QuestionnaireNotAnsweredException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCodes.QUESTIONNAIRE_NOT_ANSWERED, message);
    }
}