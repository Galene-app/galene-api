package br.com.galene.api.domain.questionnaire.dto;

import br.com.galene.api.domain.questionnaire.entity.QuestionEntity;
import br.com.galene.api.domain.questionnaire.enums.QuestionCode;

import java.util.List;

public record QuestionDto(
        Long id,
        QuestionCode code,
        String text,
        Integer displayOrder,
        Integer minChoices,
        Integer maxChoices,
        Boolean optional,
        List<OptionDto> options
) {

    public QuestionDto(QuestionEntity question) {
        this(question.getId(), question.getQuestionCode(), question.getQuestionText(), question.getDisplayOrder(),
                question.getMinChoices(), question.getMaxChoices(), question.getMinChoices() == 0,
                question.getOptions().stream().map(OptionDto::new).toList());
    }
}
