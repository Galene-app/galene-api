package br.com.galene.api.domain.sound.dto;

import br.com.galene.api.domain.sound.entity.SoundEntity;
import br.com.galene.api.domain.sound.enums.SoundType;

public record SoundDto(
        Long id,
        String name,
        SoundType type,
        String typeLabel,
        Integer durationSec,
        Boolean premiumOnly,
        Boolean requiresHeadphones,
        String mediaUrl
) {

    public SoundDto(SoundEntity sound) {
        this(sound.getId(), sound.getSoundName(), sound.getSoundType(), sound.getSoundType().getLabel(), sound.getDurationSec(),
                sound.getPremiumOnly(), sound.getSoundType().isRequiresHeadphones(), sound.getMediaUrl());
    }
}
