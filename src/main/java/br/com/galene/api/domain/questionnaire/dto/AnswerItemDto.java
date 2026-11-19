package br.com.galene.api.domain.questionnaire.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnswerItemDto(
        @Schema(example = "1")
        @NotNull(message = "Informe a pergunta")
        Long questionId,

        @Schema(example = "[1, 2]", description = "Opções escolhidas; vazio só na pergunta opcional")
        @NotNull(message = "Informe as opções escolhidas")
        List<@NotNull(message = "Opção inválida") Long> optionIds
) { }
