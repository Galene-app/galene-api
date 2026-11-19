package br.com.galene.api.common.security;

import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.service.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var tokenJwt = getToken(request);
        if (tokenJwt != null) authenticate(tokenJwt);

        filterChain.doFilter(request, response);
    }

    private void authenticate(String tokenJwt) {
        try {
            UserSecurity user = tokenService.getSubject(tokenJwt);
            var authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JWTVerificationException ex) {
            log.debug("Access token inválido: {}", ex.getClass().getSimpleName());
            SecurityContextHolder.clearContext();
        }
    }

    private String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) return null;

        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
