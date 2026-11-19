package br.com.galene.api.common.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class SecureRandomCodeGenerator implements SecureCodeGenerator {

    private static final int DECIMAL_RADIX = 10;
    private static final int MAX_NUMERIC_DIGITS = 9;
    private static final int MIN_RANDOM_BYTES = 16;
    private static final String INVALID_DIGITS_MESSAGE = "Os digitos devem estar entre 1 e " + MAX_NUMERIC_DIGITS;
    private static final String LOW_ENTROPY_MESSAGE = "randomBytes deve ter pelo menos " + MIN_RANDOM_BYTES;
    private static final String NUMERIC_CODE_FORMAT = "%%0%dd";

    private static final List<String> PASSWORD_GROUPS = List.of(
            "ABCDEFGHJKLMNPQRSTUVWXYZ", "abcdefghijkmnpqrstuvwxyz", "23456789", "@#$%*!?");
    private static final String PASSWORD_ALPHABET = String.join("", PASSWORD_GROUPS);
    private static final String SHORT_PASSWORD_MESSAGE = "A senha temporária deve ter pelo menos " + PASSWORD_GROUPS.size() + " caracteres";

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

    @Override
    public String temporaryPassword(int length) {
        if (length < PASSWORD_GROUPS.size())
            throw new IllegalArgumentException(SHORT_PASSWORD_MESSAGE);

        var characters = new ArrayList<Character>(length);
        PASSWORD_GROUPS.forEach(group -> characters.add(randomChar(group)));
        while (characters.size() < length) characters.add(randomChar(PASSWORD_ALPHABET));
        Collections.shuffle(characters, random);

        var password = new StringBuilder(length);
        characters.forEach(password::append);
        return password.toString();
    }

    private char randomChar(String alphabet) {
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }
}
