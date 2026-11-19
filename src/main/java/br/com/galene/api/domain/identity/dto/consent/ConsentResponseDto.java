package br.com.galene.api.domain.identity.dto.consent;

import br.com.galene.api.domain.identity.entity.ConsentEntity;
import br.com.galene.api.domain.identity.enums.TermType;

import java.time.LocalDateTime;

public record ConsentResponseDto(
        TermType termType,
        String termVersion,
        Boolean accepted,
        LocalDateTime answeredAt
) {

    public ConsentResponseDto(ConsentEntity consent) {
        this(consent.getTermType(), consent.getTermVersion(), consent.getAccepted(), consent.getAnsweredAt());
    }
}
