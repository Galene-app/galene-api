package br.com.galene.api.domain.reporting.dto;

import br.com.galene.api.domain.monitoring.enums.LocationType;

public record LocationExposureDto(
        LocationType locationType,
        Long seconds
) { }
