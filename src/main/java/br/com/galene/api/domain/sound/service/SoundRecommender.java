package br.com.galene.api.domain.sound.service;

import br.com.galene.api.domain.questionnaire.enums.OptionCode;
import br.com.galene.api.domain.sound.constants.SoundMessages;
import br.com.galene.api.domain.sound.dto.SoundRecommendation;
import br.com.galene.api.domain.sound.enums.SoundType;
import org.springframework.stereotype.Component;

import java.util.*;

import static br.com.galene.api.domain.questionnaire.enums.OptionCode.*;

@Component
public class SoundRecommender {

    private static final Map<OptionCode, SoundRecommendation> GOAL_SOUNDS = Map.of(
            GOAL_FOCUS, new SoundRecommendation(SoundType.LOFI, SoundMessages.REASON_FOCUS),
            GOAL_RELAX, new SoundRecommendation(SoundType.NATURE, SoundMessages.REASON_RELAX),
            GOAL_SLEEP, new SoundRecommendation(SoundType.PINK_NOISE, SoundMessages.REASON_SLEEP),
            GOAL_COMFORT, new SoundRecommendation(SoundType.WHITE_NOISE, SoundMessages.REASON_COMFORT),
            GOAL_PROTECT, new SoundRecommendation(SoundType.BROWN_NOISE, SoundMessages.REASON_PROTECT));

    private static final Map<OptionCode, SoundType> PREFERRED_STYLES = Map.of(
            SOUND_LOFI, SoundType.LOFI, SOUND_NATURE, SoundType.NATURE, SOUND_BINAURAL, SoundType.BINAURAL);

    private static final Set<OptionCode> HIGH_SENSITIVITY = Set.of(IMPACT_4, IMPACT_5, HEALTH_ADHD, HEALTH_ASD);
    private static final Set<OptionCode> SPEECH_TRIGGERS = Set.of(TRIGGER_CONVERSATION, TRIGGER_REPETITIVE);

    public List<SoundRecommendation> recommend(Collection<OptionCode> chosenOptions) {
        var codes = EnumSet.noneOf(OptionCode.class);
        codes.addAll(chosenOptions);
        var recommendations = new LinkedHashMap<SoundType, String>();

        codes.stream().filter(GOAL_SOUNDS::containsKey).findFirst().ifPresent(goal -> add(recommendations, GOAL_SOUNDS.get(goal)));
        if (codes.stream().anyMatch(HIGH_SENSITIVITY::contains))
            recommendations.putIfAbsent(SoundType.BROWN_NOISE, SoundMessages.REASON_SENSITIVITY);

        addPreferences(codes, recommendations);

        codes.stream().filter(GOAL_SOUNDS::containsKey).forEach(goal -> add(recommendations, GOAL_SOUNDS.get(goal)));
        if (codes.contains(GOAL_FOCUS)) recommendations.putIfAbsent(SoundType.BINAURAL, SoundMessages.REASON_DEEP_FOCUS);
        if (codes.stream().anyMatch(SPEECH_TRIGGERS::contains)) recommendations.putIfAbsent(SoundType.WHITE_NOISE, SoundMessages.REASON_CONVERSATION);
        if (codes.contains(TRIGGER_CONTINUOUS)) {
            recommendations.putIfAbsent(SoundType.PINK_NOISE, SoundMessages.REASON_CONTINUOUS);
            recommendations.putIfAbsent(SoundType.BROWN_NOISE, SoundMessages.REASON_CONTINUOUS);
        }
        if (codes.contains(HEALTH_ANXIETY)) recommendations.putIfAbsent(SoundType.NATURE, SoundMessages.REASON_TENSION);
        if (codes.contains(TIME_DAWN)) recommendations.putIfAbsent(SoundType.PINK_NOISE, SoundMessages.REASON_SLEEP);

        if (codes.contains(SOUND_SILENCE)) keepOnlyNoiseOrChosenStyles(codes, recommendations);
        if (recommendations.isEmpty()) addDefault(recommendations);

        return recommendations.entrySet().stream().map(entry -> new SoundRecommendation(entry.getKey(), entry.getValue())).toList();
    }

    private static void addPreferences(Set<OptionCode> codes, Map<SoundType, String> recommendations) {
        codes.stream().filter(PREFERRED_STYLES::containsKey)
                .forEach(code -> recommendations.putIfAbsent(PREFERRED_STYLES.get(code), SoundMessages.REASON_PREFERENCE));
        if (codes.contains(SOUND_NOISE) || codes.contains(SOUND_SILENCE))
            recommendations.putIfAbsent(preferredNoise(codes), SoundMessages.REASON_PREFERENCE);
        if (codes.contains(SOUND_ANY)) addDefault(recommendations);
    }

    private static SoundType preferredNoise(Set<OptionCode> codes) {
        if (codes.stream().anyMatch(SPEECH_TRIGGERS::contains)) return SoundType.WHITE_NOISE;
        if (codes.contains(GOAL_SLEEP) || codes.contains(TIME_DAWN)) return SoundType.PINK_NOISE;
        return SoundType.BROWN_NOISE;
    }

    private static void keepOnlyNoiseOrChosenStyles(Set<OptionCode> codes, Map<SoundType, String> recommendations) {
        var chosenStyles = codes.stream().filter(PREFERRED_STYLES::containsKey).map(PREFERRED_STYLES::get).toList();
        recommendations.keySet().removeIf(type -> !type.isNoise() && !chosenStyles.contains(type));
    }

    private static void addDefault(Map<SoundType, String> recommendations) {
        recommendations.putIfAbsent(SoundType.LOFI, SoundMessages.REASON_DEFAULT);
        recommendations.putIfAbsent(SoundType.PINK_NOISE, SoundMessages.REASON_DEFAULT);
    }

    private static void add(Map<SoundType, String> recommendations, SoundRecommendation recommendation) {
        recommendations.putIfAbsent(recommendation.type(), recommendation.reason());
    }
}
