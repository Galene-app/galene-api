package br.com.galene.api.config.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * Impede que a aplicação suba com o 2FA desligado fora dos profiles dev/test.
 * Sem profile (o que inclui um deploy em que esqueceram de configurar) a aplicação não sobe.
 */
@Log4j2
@Component
public class MfaSafetyGuard {

    static final Set<String> MFA_OPTIONAL_PROFILES = Set.of("dev", "test");
    static final String MFA_DISABLED_OUTSIDE_DEV_MESSAGE = "MFA não pode ser desabilitado fora dos perfis dev/test";

    public MfaSafetyGuard(SecurityProperties properties, Environment environment) {
        verify(properties.isHasMfa(), environment.getActiveProfiles());
    }

    static void verify(boolean hasMfa, String[] activeProfiles) {
        if (hasMfa) return;

        boolean onlyOptionalProfiles = activeProfiles.length > 0
                && Arrays.stream(activeProfiles).allMatch(MFA_OPTIONAL_PROFILES::contains);
        if (!onlyOptionalProfiles)
            throw new IllegalStateException(MFA_DISABLED_OUTSIDE_DEV_MESSAGE);

        log.warn("=================================================");
        log.warn("  2FA DESABILITADO ");
        log.warn("=================================================");
    }
}
