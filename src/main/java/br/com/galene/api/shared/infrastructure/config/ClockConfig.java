package br.com.galene.api.shared.infrastructure.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    public static final String ZONE_ID_NAME = "America/Sao_Paulo";
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_NAME);

    @Bean
    public Clock clock() {
        return Clock.system(ZONE_ID);
    }
}
