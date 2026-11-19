package br.com.galene.api.domain.identity.security;

import br.com.galene.api.domain.identity.enums.ProfileType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record UserSecurity(Long userId, ProfileType role, Long sessionId) {

    public static final String ROLE_PREFIX = "ROLE_";

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
    }
}
