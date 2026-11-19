package br.com.galene.api.domain.identity.dto.event;

import br.com.galene.api.domain.identity.enums.TermType;

public record ConsentRevokedEvent(Long userId, Long patientId, TermType termType) { }
