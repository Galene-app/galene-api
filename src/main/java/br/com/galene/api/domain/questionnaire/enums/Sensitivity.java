package br.com.galene.api.domain.questionnaire.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sensitivity {
    LOW(PlayerStartLevel.INTENSE, Dampening.LIGHT),
    MEDIUM(PlayerStartLevel.INTENSE, Dampening.MEDIUM),
    HIGH(PlayerStartLevel.MODERATE, Dampening.STRONG);

    private final PlayerStartLevel startLevel;
    private final Dampening dampening;
}
