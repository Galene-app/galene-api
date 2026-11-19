package br.com.galene.api.domain.questionnaire.service;

import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.service.ConsentService;
import br.com.galene.api.domain.identity.service.UserService;
import br.com.galene.api.domain.questionnaire.constants.QuestionnaireMessages;
import br.com.galene.api.domain.questionnaire.dto.AnswerItemDto;
import br.com.galene.api.domain.questionnaire.dto.QuestionDto;
import br.com.galene.api.domain.questionnaire.dto.QuestionnaireAnswersDto;
import br.com.galene.api.domain.questionnaire.dto.SoundProfileDto;
import br.com.galene.api.domain.questionnaire.entity.AnswerEntity;
import br.com.galene.api.domain.questionnaire.entity.QuestionEntity;
import br.com.galene.api.domain.questionnaire.entity.QuestionOptionEntity;
import br.com.galene.api.domain.questionnaire.exception.*;
import br.com.galene.api.domain.questionnaire.repository.AnswerRepository;
import br.com.galene.api.domain.questionnaire.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ConsentService consentService;
    private final SoundProfileCalculator profileCalculator;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionnaire(Long userId) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        return activeQuestions().stream()
                .filter(question -> canAnswer(userId, question))
                .map(QuestionDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnswerItemDto> getAnswers(Long userId) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        var answersByQuestion = answerRepository.findByPatientId(findPatient(userId).getId()).stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getDisplayOrder(), TreeMap::new, Collectors.toList()));

        return answersByQuestion.values().stream()
                .map(answers -> new AnswerItemDto(answers.getFirst().getQuestion().getId(),
                        answers.stream().map(answer -> answer.getOption().getId()).sorted().toList()))
                .toList();
    }

    @Transactional
    public SoundProfileDto saveAnswers(Long userId, QuestionnaireAnswersDto request) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        var user = userService.getUserById(userId);
        var patient = findPatient(userId);
        var questions = activeQuestions().stream().collect(Collectors.toMap(QuestionEntity::getId, Function.identity()));

        var chosenOptions = new ArrayList<QuestionOptionEntity>();
        var answeredQuestions = new HashSet<Long>();
        for (var item : request.answers()) {
            var question = questions.get(item.questionId());
            if (question == null)
                throw new QuestionNotFoundException(QuestionnaireMessages.QUESTION_NOT_FOUND.formatted(item.questionId()));
            if (!answeredQuestions.add(question.getId()))
                throw new InvalidChoiceCountException(QuestionnaireMessages.DUPLICATED_QUESTION.formatted(question.getDisplayOrder()));

            var options = validateChoices(question, item.optionIds());
            if (!options.isEmpty() && question.getRequiredConsent() != null)
                consentService.requireConsent(userId, question.getRequiredConsent());
            chosenOptions.addAll(options);
        }
        requireMandatoryAnswers(userId, questions.values(), answeredQuestions);

        var now = LocalDateTime.now(clock);
        answerRepository.deleteAllOfPatient(patient.getId());
        answerRepository.saveAll(chosenOptions.stream().map(option -> new AnswerEntity(patient, option, user, now)).toList());

        log.info("Usuário {} respondeu o questionário ({} opções)", userId, chosenOptions.size());
        return profileCalculator.calculate(chosenOptions.stream().map(QuestionOptionEntity::getOptionCode).toList());
    }

    @Transactional(readOnly = true)
    public SoundProfileDto getSoundProfile(Long userId) {
        consentService.requireConsent(userId, TermType.DATA_USE);
        var answers = answerRepository.findByPatientId(findPatient(userId).getId());
        if (answers.isEmpty())
            throw new QuestionnaireNotAnsweredException(QuestionnaireMessages.QUESTIONNAIRE_NOT_ANSWERED);

        return profileCalculator.calculate(answers.stream().map(answer -> answer.getOption().getOptionCode()).toList());
    }

    private List<QuestionOptionEntity> validateChoices(QuestionEntity question, List<Long> optionIds) {
        var optionsById = question.getOptions().stream().collect(Collectors.toMap(QuestionOptionEntity::getId, Function.identity()));
        var chosen = new LinkedHashSet<QuestionOptionEntity>();
        for (var optionId : optionIds) {
            var option = optionsById.get(optionId);
            if (option == null)
                throw new OptionNotInQuestionException(QuestionnaireMessages.OPTION_NOT_IN_QUESTION.formatted(optionId, question.getDisplayOrder()));
            chosen.add(option);
        }

        validateChoiceCount(question, chosen.size());
        chosen.stream().filter(option -> option.getOptionCode().isExclusive()).findFirst().ifPresent(exclusive -> {
            if (chosen.size() > 1)
                throw new InvalidChoiceCountException(QuestionnaireMessages.EXCLUSIVE_OPTION.formatted(exclusive.getOptionText()));
        });
        return List.copyOf(chosen);
    }

    private static void validateChoiceCount(QuestionEntity question, int count) {
        var min = question.getMinChoices();
        var max = question.getMaxChoices();
        if (count >= min && (max == null || count <= max)) return;

        var order = question.getDisplayOrder();
        if (max == null) throw new InvalidChoiceCountException(QuestionnaireMessages.MIN_CHOICES.formatted(order, min));
        if (min.equals(max)) throw new InvalidChoiceCountException(QuestionnaireMessages.EXACT_CHOICES.formatted(order, max));
        throw new InvalidChoiceCountException(QuestionnaireMessages.CHOICES_RANGE.formatted(order, min, max));
    }

    private void requireMandatoryAnswers(Long userId, Collection<QuestionEntity> questions, Set<Long> answeredQuestions) {
        questions.stream()
                .filter(question -> question.getMinChoices() > 0 && !answeredQuestions.contains(question.getId()))
                .filter(question -> canAnswer(userId, question))
                .min(Comparator.comparing(QuestionEntity::getDisplayOrder))
                .ifPresent(question -> {
                    throw new UnansweredQuestionException(QuestionnaireMessages.QUESTION_NOT_ANSWERED
                            .formatted(question.getDisplayOrder(), question.getQuestionText()));
                });
    }

    private boolean canAnswer(Long userId, QuestionEntity question) {
        return question.getRequiredConsent() == null || consentService.hasConsent(userId, question.getRequiredConsent());
    }

    private List<QuestionEntity> activeQuestions() {
        return questionRepository.findAllWithOptions().stream().filter(QuestionEntity::getActive).toList();
    }

    private PatientEntity findPatient(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(QuestionnaireMessages.PATIENT_NOT_FOUND));
    }
}
