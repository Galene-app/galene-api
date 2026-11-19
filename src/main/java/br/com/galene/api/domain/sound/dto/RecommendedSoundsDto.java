package br.com.galene.api.domain.sound.dto;

import br.com.galene.api.domain.sound.enums.SoundType;

import java.util.List;

public record RecommendedSoundsDto(
        SoundType type,
        String typeLabel,
        String reason,
        Boolean requiresHeadphones,
        List<SoundDto> sounds
) { }
