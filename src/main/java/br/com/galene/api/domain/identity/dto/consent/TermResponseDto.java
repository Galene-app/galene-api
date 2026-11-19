package br.com.galene.api.domain.identity.dto.consent;

import br.com.galene.api.domain.identity.entity.TermEntity;
import br.com.galene.api.domain.identity.enums.TermType;

import java.time.LocalDateTime;

public record TermResponseDto(
        TermType termType,
        String termVersion,
        String title,
        String content,
        Boolean mandatory,
        LocalDateTime publishedAt
) {

    public TermResponseDto(TermEntity term) {
        this(term.getTermType(), term.getTermVersion(), term.getTitle(), term.getContent(), term.getMandatory(), term.getPublishedAt());
    }
}
