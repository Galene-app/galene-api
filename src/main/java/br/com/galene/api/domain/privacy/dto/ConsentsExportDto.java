package br.com.galene.api.domain.privacy.dto;

import br.com.galene.api.domain.identity.dto.consent.ConsentResponseDto;

import java.util.List;

public record ConsentsExportDto(
        List<ConsentResponseDto> current,
        List<ConsentResponseDto> history
) { }
