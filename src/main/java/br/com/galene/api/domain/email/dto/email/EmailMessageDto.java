package br.com.galene.api.domain.email.dto.email;

import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.domain.email.enums.EmailType;

public record EmailMessageDto(
        String toAddress,
        String toName,
        String subject,
        String html,
        String plain,
        EmailType type
) {
    // O corpo carrega código/link de uso único: nunca pode aparecer em log
    @Override
    public String toString() {
        return "EmailMessageDto[type=" + type + ", to=" + EmailHelper.mask(toAddress) + "]";
    }
}
