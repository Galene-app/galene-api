package br.com.galene.api.common.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureRandomCodeGenerator implements SecureCodeGenerator {

    private static final int DECIMAL_RADIX = 10;
    private static final int MAX_NUMERIC_DIGITS = 9;
    private static final int MIN_RANDOM_BYTES = 16;
    private static final String INVALID_DIGITS_MESSAGE = "Os digitos devem estar entre 1 e " + MAX_NUMERIC_DIGITS;
    private static final String LOW_ENTROPY_MESSAGE = "randomBytes deve ter pelo menos " + MIN_RANDOM_BYTES;
    // Gera o padrão "%0Nd" (ex.: %06d), que completa o código com zeros à esquerda
    private static final String NUMERIC_CODE_FORMAT = "%%0%dd";

    private final SecureRandom random = new SecureRandom();
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();

    @Override
    public String numericCode(int digits) {
        if (digits < 1 || digits > MAX_NUMERIC_DIGITS)
            throw new IllegalArgumentException(INVALID_DIGITS_MESSAGE);

        int bound = (int) Math.pow(DECIMAL_RADIX, digits);
        return String.format(NUMERIC_CODE_FORMAT.formatted(digits), random.nextInt(bound));
    }

    @Override
    public String opaqueToken(int randomBytes) {
        if (randomBytes < MIN_RANDOM_BYTES)
            throw new IllegalArgumentException(LOW_ENTROPY_MESSAGE);

        byte[] bytes = new byte[randomBytes];
        random.nextBytes(bytes);
        return urlEncoder.encodeToString(bytes);
    }
}
