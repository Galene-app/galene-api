package br.com.galene.api.common.helpers;

import java.util.Locale;

public final class EmailHelper {

    private static final String MASK = "***";
    private static final char AT = '@';

    private EmailHelper() { }

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static String mask(String email) {
        if (email == null) return MASK;

        int at = email.indexOf(AT);
        if (at < 1) return MASK;

        return email.charAt(0) + MASK + email.substring(at);
    }
}
