package br.com.galene.api.config.security;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.security.SecurityExceptionHandler;
import br.com.galene.api.common.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] DOCUMENTATION_PATHS = {"/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};
    private static final String ERROR_PATH = "/error";
    private static final String ALL_PATHS = "/**";
    private static final List<String> CORS_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> CORS_HEADERS = List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE);
    private static final List<String> CORS_EXPOSED_HEADERS = List.of(HttpHeaders.LOCATION);

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JsonMapper jsonMapper) {
        var errorHandler = new SecurityExceptionHandler(jsonMapper);
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(errorHandler).accessDeniedHandler(errorHandler))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.POST, ApiPaths.AUTH_LOGOUT).authenticated()
                                .requestMatchers(ApiPaths.AUTH_ALL).permitAll()
                                .requestMatchers(HttpMethod.GET, ApiPaths.TERMS, ApiPaths.NOISE_LEVELS, ApiPaths.SOUNDS).permitAll()
                                .requestMatchers(DOCUMENTATION_PATHS).permitAll()
                                .requestMatchers(ERROR_PATH).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(SecurityProperties properties) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.getCorsAllowedOrigins());
        configuration.setAllowedMethods(CORS_METHODS);
        configuration.setAllowedHeaders(CORS_HEADERS);
        configuration.setExposedHeaders(CORS_EXPOSED_HEADERS);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ALL_PATHS, configuration);
        return source;
    }
}
