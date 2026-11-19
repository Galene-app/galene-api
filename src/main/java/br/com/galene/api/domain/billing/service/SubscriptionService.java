package br.com.galene.api.domain.billing.service;

import br.com.galene.api.domain.billing.enums.PlanType;
import br.com.galene.api.domain.billing.enums.SubscriptionStatus;
import br.com.galene.api.domain.billing.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public boolean isPremium(Long userId) {
        return subscriptionRepository.hasValidSubscription(userId, PlanType.PREMIUM, SubscriptionStatus.ACTIVE, LocalDate.now(clock));
    }
}
