package br.com.galene.api.domain.monitoring.dto.event;

import java.math.BigDecimal;

public record CriticalExposureEvent(Long userId, Long alertId, BigDecimal peakDb, Integer criticalReadings) { }
