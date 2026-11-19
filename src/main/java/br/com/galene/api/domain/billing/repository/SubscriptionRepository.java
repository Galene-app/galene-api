package br.com.galene.api.domain.billing.repository;

import br.com.galene.api.domain.billing.entity.SubscriptionEntity;
import br.com.galene.api.domain.billing.enums.PlanType;
import br.com.galene.api.domain.billing.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    @Query("""
            SELECT COUNT(s) > 0 FROM SubscriptionEntity s
            WHERE s.holderUserId = :userId AND s.planType = :plan AND s.status = :status
              AND s.startDate <= :today AND (s.endDate IS NULL OR s.endDate >= :today)""")
    boolean hasValidSubscription(@Param("userId") Long userId, @Param("plan") PlanType plan,
                                 @Param("status") SubscriptionStatus status, @Param("today") LocalDate today);
}
