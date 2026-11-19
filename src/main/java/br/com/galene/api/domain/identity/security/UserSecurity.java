package br.com.galene.api.domain.identity.security;

import br.com.galene.api.domain.identity.enums.ProfileType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Usuário autenticado, montado a partir das claims do JWT (sem consulta ao banco).
 * Os controllers o recebem com {@code @AuthenticationPrincipal}.
 */
public record UserSecurity(Long userId, ProfileType role) {

    public static final String ROLE_PREFIX = "ROLE_";

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
    }
}
