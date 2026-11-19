package br.com.galene.api.domain.questionnaire.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionCode {
    GOAL_FOCUS(false),
    GOAL_RELAX(false),
    GOAL_SLEEP(false),
    GOAL_COMFORT(false),
    GOAL_PROTECT(false),

    ENV_WORK(false),
    ENV_SCHOOL(false),
    ENV_TRANSPORT(false),
    ENV_CROWDED(false),
    ENV_STREET(false),
    ENV_HOME(false),

    TRIGGER_CROWDS(false),
    TRIGGER_SUDDEN(false),
    TRIGGER_CONTINUOUS(false),
    TRIGGER_CONVERSATION(false),
    TRIGGER_REPETITIVE(false),
    TRIGGER_OTHER(false),

    IMPACT_1(false),
    IMPACT_2(false),
    IMPACT_3(false),
    IMPACT_4(false),
    IMPACT_5(false),

    SOUND_LOFI(false),
    SOUND_NATURE(false),
    SOUND_NOISE(false),
    SOUND_BINAURAL(false),
    SOUND_SILENCE(false),
    SOUND_ANY(true),

    COPING_VOLUME(false),
    COPING_ANC(false),
    COPING_EARPLUG(false),
    COPING_AVOID(false),
    COPING_NOTHING(false),

    TIME_MORNING(false),
    TIME_AFTERNOON(false),
    TIME_NIGHT(false),
    TIME_DAWN(false),
    TIME_VARIES(false),

    HEALTH_ADHD(false),
    HEALTH_ASD(false),
    HEALTH_ANXIETY(false),
    HEALTH_SENSITIVITY(false),
    HEALTH_UNDISCLOSED(true);

    private final boolean exclusive;
}
