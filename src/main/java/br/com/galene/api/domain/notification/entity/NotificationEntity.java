package br.com.galene.api.domain.notification.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.domain.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long id;

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_alert")
    private Long alertId;

    @Enumerated(EnumType.STRING)
    private NotificationType notifType;

    private String message;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean isRead;

    private LocalDateTime createdAt;

    public NotificationEntity(Long userId, Long alertId, NotificationType notifType, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.alertId = alertId;
        this.notifType = notifType;
        this.message = message;
        this.isRead = false;
        this.createdAt = createdAt;
    }
}
