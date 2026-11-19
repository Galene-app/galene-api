package br.com.galene.api.domain.questionnaire.service;

import br.com.galene.api.domain.questionnaire.dto.SoundProfileDto;
import br.com.galene.api.domain.questionnaire.enums.*;
import org.springframework.stereotype.Component;

import java.util.*;

import static br.com.galene.api.domain.questionnaire.enums.OptionCode.*;

@Component
public class SoundProfileCalculator {

    private static final Map<OptionCode, SoundGoal> GOALS = Map.of(
            GOAL_FOCUS, SoundGoal.FOCUS, GOAL_RELAX, SoundGoal.RELAX, GOAL_SLEEP, SoundGoal.SLEEP,
            GOAL_COMFORT, SoundGoal.COMFORT, GOAL_PROTECT, SoundGoal.PROTECT);

    private static final Map<OptionCode, NoiseEnvironment> ENVIRONMENTS = Map.of(
            ENV_WORK, NoiseEnvironment.WORK, ENV_SCHOOL, NoiseEnvironment.SCHOOL, ENV_TRANSPORT, NoiseEnvironment.TRANSPORT,
            ENV_CROWDED, NoiseEnvironment.CROWDED, ENV_STREET, NoiseEnvironment.STREET, ENV_HOME, NoiseEnvironment.HOME);

    private static final Map<OptionCode, MaskingType> MASKING_TYPES = Map.of(
            TRIGGER_CONVERSATION, MaskingType.SPEECH, TRIGGER_REPETITIVE, MaskingType.SPEECH,
            TRIGGER_CONTINUOUS, MaskingType.PINK_BROWN_NOISE);

    private static final Map<OptionCode, Sensitivity> SENSITIVITY = Map.of(
            IMPACT_1, Sensitivity.LOW, IMPACT_2, Sensitivity.LOW, IMPACT_3, Sensitivity.MEDIUM,
            IMPACT_4, Sensitivity.HIGH, IMPACT_5, Sensitivity.HIGH);

    private static final Map<OptionCode, List<SoundStyle>> SOUNDS = Map.of(
            SOUND_LOFI, List.of(SoundStyle.LOFI), SOUND_NATURE, List.of(SoundStyle.NATURE),
            SOUND_NOISE, List.of(SoundStyle.NOISE), SOUND_BINAURAL, List.of(SoundStyle.BINAURAL),
            SOUND_SILENCE, List.of(SoundStyle.MASKING), SOUND_ANY, List.of(SoundStyle.LOFI, SoundStyle.MASKING));

    private static final Map<OptionCode, CalmPeriod> CALM_PERIODS = Map.of(
            TIME_MORNING, CalmPeriod.MORNING, TIME_AFTERNOON, CalmPeriod.AFTERNOON, TIME_NIGHT, CalmPeriod.NIGHT,
            TIME_DAWN, CalmPeriod.DAWN, TIME_VARIES, CalmPeriod.VARIES);

    private static final Set<OptionCode> GENTLE_TRANSITIONS = Set.of(HEALTH_ASD, HEALTH_ANXIETY);

    public SoundProfileDto calculate(Collection<OptionCode> chosenOptions) {
        var codes = EnumSet.noneOf(OptionCode.class);
        codes.addAll(chosenOptions);

        var goals = collect(codes, GOALS);
        var sensitivity = collect(codes, SENSITIVITY).stream().findFirst().orElse(Sensitivity.MEDIUM);
        var sounds = codes.stream().flatMap(code -> SOUNDS.getOrDefault(code, List.of()).stream()).distinct().toList();

        return new SoundProfileDto(
                goals.isEmpty() ? null : goals.getFirst(),
                goals,
                sensitivity,
                sensitivity.getStartLevel(),
                sensitivity.getDampening(),
                sounds,
                collect(codes, MASKING_TYPES),
                codes.contains(TRIGGER_SUDDEN),
                collect(codes, ENVIRONMENTS),
                codes.contains(COPING_VOLUME),
                codes.contains(COPING_AVOID),
                collect(codes, CALM_PERIODS),
                codes.stream().anyMatch(GENTLE_TRANSITIONS::contains)
        );
    }

    private static <T> List<T> collect(Set<OptionCode> codes, Map<OptionCode, T> mapping) {
        return codes.stream().map(mapping::get).filter(Objects::nonNull).distinct().toList();
    }
}
