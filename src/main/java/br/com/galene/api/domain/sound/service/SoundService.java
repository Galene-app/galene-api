package br.com.galene.api.domain.sound.service;

import br.com.galene.api.domain.billing.service.SubscriptionService;
import br.com.galene.api.domain.questionnaire.service.QuestionnaireService;
import br.com.galene.api.domain.sound.dto.RecommendedSoundsDto;
import br.com.galene.api.domain.sound.dto.SoundDto;
import br.com.galene.api.domain.sound.entity.SoundEntity;
import br.com.galene.api.domain.sound.enums.SoundType;
import br.com.galene.api.domain.sound.repository.SoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoundService {

    private final SoundRepository soundRepository;
    private final QuestionnaireService questionnaireService;
    private final SubscriptionService subscriptionService;
    private final SoundRecommender soundRecommender;

    @Transactional(readOnly = true)
    public List<SoundDto> getCatalog(SoundType type) {
        var sounds = type == null ? soundRepository.findAllByOrderByIdAsc() : soundRepository.findBySoundTypeOrderByIdAsc(type);
        return sounds.stream().map(SoundDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendedSoundsDto> getRecommendedSounds(Long userId) {
        var recommendations = soundRecommender.recommend(questionnaireService.getChosenOptions(userId));
        var premium = subscriptionService.isPremium(userId);

        var soundsByType = soundRepository.findBySoundTypeInOrderByIdAsc(recommendations.stream().map(recommendation -> recommendation.type()).toList())
                .stream()
                .filter(sound -> premium || !sound.getPremiumOnly())
                .collect(Collectors.groupingBy(SoundEntity::getSoundType));

        return recommendations.stream()
                .map(recommendation -> new RecommendedSoundsDto(
                        recommendation.type(),
                        recommendation.type().getLabel(),
                        recommendation.reason(),
                        recommendation.type().isRequiresHeadphones(),
                        soundsByType.getOrDefault(recommendation.type(), List.of()).stream().map(SoundDto::new).toList()))
                .toList();
    }
}
