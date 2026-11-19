package br.com.galene.api.domain.sound.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SoundType {
    WHITE_NOISE("Ruído branco", false),
    PINK_NOISE("Ruído rosa", false),
    BROWN_NOISE("Ruído marrom", false),
    LOFI("Lo-fi", false),
    BINAURAL("Batidas binaurais", true),
    NATURE("Chuva e natureza", false);

    private final String label;
    private final boolean requiresHeadphones;

    public boolean isNoise() {
        return this == WHITE_NOISE || this == PINK_NOISE || this == BROWN_NOISE;
    }
}
