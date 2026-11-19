package br.com.galene.api.domain.email.dto.event;

import br.com.galene.api.domain.email.dto.email.EmailMessageDto;

public record EmailRequestedEvent(EmailMessageDto email) { }
