package br.com.galene.api.domain.notification.dto;

import br.com.galene.api.domain.notification.entity.NotificationEntity;
import br.com.galene.api.domain.notification.enums.NotificationType;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public record NotificationDto(
        Long id,
        NotificationType type,
        String message,
        Boolean read,
        OffsetDateTime createdAt
) {

    public NotificationDto(NotificationEntity notification, ZoneId zone) {
        this(notification.getId(), notification.getNotifType(), notification.getMessage(), notification.getIsRead(),
                notification.getCreatedAt().atZone(zone).toOffsetDateTime());
    }
}
