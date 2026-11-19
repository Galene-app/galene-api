package br.com.galene.api.domain.reporting.dto.event;

import java.time.LocalDate;

public record DailyReportGeneratedEvent(Long userId, LocalDate date) { }
