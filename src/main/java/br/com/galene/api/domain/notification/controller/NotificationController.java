package br.com.galene.api.domain.notification.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.PageResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.notification.constants.NotificationMessages;
import br.com.galene.api.domain.notification.dto.NotificationDto;
import br.com.galene.api.domain.notification.dto.NotificationReadDto;
import br.com.galene.api.domain.notification.dto.NotificationsReadResultDto;
import br.com.galene.api.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notificações")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequestMapping(ApiPaths.USERS_ME_NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final NotificationService notificationService;

    @Operation(summary = "Lista as notificações do usuário, da mais recente para a mais antiga")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationDto>>> getNotifications(
            @AuthenticationPrincipal UserSecurity user,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable) {
        var notifications = notificationService.getNotifications(user.userId(), unreadOnly, pageable);
        return ResponseEntity.ok(new SuccessResult<>(NotificationMessages.NOTIFICATIONS_FOUND, notifications));
    }

    @Operation(summary = "Marca uma notificação como lida ou não lida")
    @PatchMapping(ApiPaths.NOTIFICATION_ID)
    public ResponseEntity<ApiResponse<NotificationDto>> updateRead(@AuthenticationPrincipal UserSecurity user,
                                                                   @PathVariable Long notificationId,
                                                                   @RequestBody @Valid NotificationReadDto request) {
        var notification = notificationService.updateRead(user.userId(), notificationId, request.read());
        return ResponseEntity.ok(new SuccessResult<>(NotificationMessages.NOTIFICATION_UPDATED, notification));
    }

    @Operation(summary = "Marca todas as notificações do usuário como lidas ou não lidas")
    @PatchMapping
    public ResponseEntity<ApiResponse<NotificationsReadResultDto>> updateReadOfAll(@AuthenticationPrincipal UserSecurity user,
                                                                                   @RequestBody @Valid NotificationReadDto request) {
        int updated = notificationService.updateReadOfAll(user.userId(), request.read());
        return ResponseEntity.ok(new SuccessResult<>(NotificationMessages.NOTIFICATIONS_UPDATED, new NotificationsReadResultDto(updated)));
    }
}
