package br.com.galene.api.domain.monitoring.dto;

import br.com.galene.api.domain.monitoring.entity.IntensityLevelEntity;

import java.math.BigDecimal;

public record NoiseLevelDto(
        Long id,
        String name,
        BigDecimal dbMin,
        BigDecimal dbMax,
        String colorHex,
        String galeneAction
) {

    public NoiseLevelDto(IntensityLevelEntity level) {
        this(level.getId(), level.getLevelName(), level.getDbMin(), level.getDbMax(), level.getColorHex(), level.getGaleneAction());
    }
}
