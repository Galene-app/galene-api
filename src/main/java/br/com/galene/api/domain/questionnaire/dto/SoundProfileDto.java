package br.com.galene.api.domain.questionnaire.dto;

import br.com.galene.api.domain.questionnaire.enums.*;

import java.util.List;

public record SoundProfileDto(
        SoundGoal primaryGoal,
        List<SoundGoal> goals,
        Sensitivity sensitivity,
        PlayerStartLevel startLevel,
        Dampening dampening,
        List<SoundStyle> sounds,
        List<MaskingType> maskingTypes,
        Boolean fastReaction,
        List<NoiseEnvironment> environments,
        Boolean safeVolumeWarnings,
        Boolean avoidanceTips,
        List<CalmPeriod> calmPeriods,
        Boolean gentleTransitions
) { }
