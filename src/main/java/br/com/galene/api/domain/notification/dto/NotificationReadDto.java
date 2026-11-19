package br.com.galene.api.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NotificationReadDto(
        @Schema(example = "true")
        @NotNull(message = "Informe se a notificação foi lida")
        Boolean read
) { }
