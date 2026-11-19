package br.com.galene.api.domain.privacy.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AnswerExportDto(
        String question,
        List<String> chosenOptions,
        OffsetDateTime answeredAt
) { }
