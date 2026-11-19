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
    @Override
    public String toString() {
        return "EmailMessageDto[type=" + type + ", to=" + EmailHelper.mask(toAddress) + "]";
    }
}
