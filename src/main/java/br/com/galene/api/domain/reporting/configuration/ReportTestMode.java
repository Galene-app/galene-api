package br.com.galene.api.domain.reporting.configuration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Log4j2
@Getter
@Component
public class ReportTestMode {

    static final Set<String> ALLOWED_PROFILES = Set.of("dev", "test");
    static final String ENABLED_OUTSIDE_DEV_MESSAGE = "O modo de teste do relatório não pode ser ligado fora dos perfis dev/test";

    private final boolean enabled;

    public ReportTestMode(@Value("${galene.reports.test-mode-enabled:false}") boolean enabled, Environment environment) {
        this.enabled = verify(enabled, environment.getActiveProfiles());
    }

    static boolean verify(boolean enabled, String[] activeProfiles) {
        if (!enabled) return false;

        boolean onlyAllowedProfiles = activeProfiles.length > 0 && Arrays.stream(activeProfiles).allMatch(ALLOWED_PROFILES::contains);
        if (!onlyAllowedProfiles)
            throw new IllegalStateException(ENABLED_OUTSIDE_DEV_MESSAGE);

        log.warn("Modo de teste do relatório LIGADO: ?testMode=true ignora as 24h de uso, o dia de hoje e o Premium");
        return true;
    }
}
