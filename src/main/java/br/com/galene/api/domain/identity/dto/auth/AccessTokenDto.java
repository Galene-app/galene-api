package br.com.galene.api.domain.identity.dto.auth;

import java.time.Instant;

public record AccessTokenDto(String token, Instant expiresAt, long expiresIn) { }
