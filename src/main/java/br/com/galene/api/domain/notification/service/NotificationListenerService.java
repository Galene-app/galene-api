package br.com.galene.api.domain.notification.service;

import br.com.galene.api.domain.identity.dto.event.AccountDeletedEvent;
import br.com.galene.api.domain.monitoring.dto.event.CriticalExposureEvent;
import br.com.galene.api.domain.notification.constants.NotificationMessages;
import br.com.galene.api.domain.notification.entity.NotificationEntity;
import br.com.galene.api.domain.notification.enums.NotificationType;
import br.com.galene.api.domain.notification.repository.NotificationRepository;
import br.com.galene.api.domain.reporting.dto.event.DailyReportGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationListenerService {

    public static final int ACCOUNT_DELETION_ORDER = 1;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String DB_FORMAT = "%.1f";
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    private final NotificationRepository notificationRepository;
    private final Clock clock;

    @EventListener
    public void onCriticalExposure(CriticalExposureEvent event) {
        var message = NotificationMessages.CRITICAL_EXPOSURE.formatted(String.format(PT_BR, DB_FORMAT, event.peakDb()));
        notificationRepository.save(new NotificationEntity(event.userId(), event.alertId(), NotificationType.CRITICAL_ALERT,
                message, LocalDateTime.now(clock)));
        log.info("Notificação de exposição crítica criada para o usuário {}", event.userId());
    }

    @EventListener
    public void onDailyReportGenerated(DailyReportGeneratedEvent event) {
        var message = NotificationMessages.DAILY_REPORT_AVAILABLE.formatted(event.date().format(DAY_FORMAT));
        notificationRepository.save(new NotificationEntity(event.userId(), null, NotificationType.DAILY_REPORT, message, LocalDateTime.now(clock)));
    }

    @Order(ACCOUNT_DELETION_ORDER)
    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event) {
        int deleted = notificationRepository.deleteAllOfUser(event.userId());
        log.info("{} notificação(ões) apagada(s) na exclusão da conta {}", deleted, event.userId());
    }
}
