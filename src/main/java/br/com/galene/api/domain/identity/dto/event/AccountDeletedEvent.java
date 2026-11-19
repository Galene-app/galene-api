package br.com.galene.api.domain.identity.dto.event;

public record AccountDeletedEvent(Long userId, Long patientId) { }
