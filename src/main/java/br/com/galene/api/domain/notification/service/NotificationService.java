package br.com.galene.api.domain.notification.service;

import br.com.galene.api.common.model.PageResponse;
import br.com.galene.api.domain.notification.constants.NotificationMessages;
import br.com.galene.api.domain.notification.dto.NotificationDto;
import br.com.galene.api.domain.notification.exception.NotificationNotFoundException;
import br.com.galene.api.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt", "id");

    private final NotificationRepository notificationRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public PageResponse<NotificationDto> getNotifications(Long userId, boolean unreadOnly, Pageable pageable) {
        var page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), NEWEST_FIRST);
        var notifications = unreadOnly
                ? notificationRepository.findByUserIdAndIsRead(userId, false, page)
                : notificationRepository.findByUserId(userId, page);
        return new PageResponse<>(notifications).map(notification -> new NotificationDto(notification, clock.getZone()));
    }

    @Transactional
    public NotificationDto updateRead(Long userId, Long notificationId, boolean read) {
        var notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(NotificationMessages.NOTIFICATION_NOT_FOUND));
        notification.setIsRead(read);
        return new NotificationDto(notification, clock.getZone());
    }

    @Transactional
    public int updateReadOfAll(Long userId, boolean read) {
        int updated = notificationRepository.updateReadOfUser(userId, read);
        log.info("Usuário {} marcou {} notificação(ões) como {}", userId, updated, read ? "lidas" : "não lidas");
        return updated;
    }
}
