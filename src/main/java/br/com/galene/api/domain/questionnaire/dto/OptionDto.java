package br.com.galene.api.domain.questionnaire.dto;

import br.com.galene.api.domain.questionnaire.entity.QuestionOptionEntity;
import br.com.galene.api.domain.questionnaire.enums.OptionCode;

public record OptionDto(
        Long id,
        OptionCode code,
        String text
) {

    public OptionDto(QuestionOptionEntity option) {
        this(option.getId(), option.getOptionCode(), option.getOptionText());
    }
}
