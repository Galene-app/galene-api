package br.com.galene.api.domain.questionnaire.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuestionnaireAnswersDto(
        @NotEmpty(message = "Envie as respostas do questionário")
        List<@Valid AnswerItemDto> answers
) { }
