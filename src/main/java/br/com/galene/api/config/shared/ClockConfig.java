package br.com.galene.api.config.shared;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Relógio único da aplicação (horário de Brasília). Os services recebem o {@link Clock}
 * por injeção, o que permite fixar a data e a hora nos testes.
 */
@Configuration
public class ClockConfig {

    public static final String ZONE_ID_NAME = "America/Sao_Paulo";
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_NAME);

    @Bean
    public Clock clock() {
        return Clock.system(ZONE_ID);
    }
}
