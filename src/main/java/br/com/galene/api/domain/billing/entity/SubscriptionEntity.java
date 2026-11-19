package br.com.galene.api.domain.billing.entity;

import br.com.galene.api.domain.billing.enums.PlanType;
import br.com.galene.api.domain.billing.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "gln_subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subscription")
    private Long id;

    @Column(name = "id_user_holder")
    private Long holderUserId;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_status")
    private SubscriptionStatus status;

    private LocalDate startDate;
    private LocalDate endDate;
}
