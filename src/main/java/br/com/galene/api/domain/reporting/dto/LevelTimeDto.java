package br.com.galene.api.domain.reporting.dto;

import java.math.BigDecimal;

public record LevelTimeDto(
        String level,
        String colorHex,
        Long seconds,
        BigDecimal percent
) { }
