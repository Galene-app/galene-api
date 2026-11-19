package br.com.galene.api.domain.sound.dto;

import br.com.galene.api.domain.sound.enums.SoundType;

public record SoundRecommendation(SoundType type, String reason) { }
