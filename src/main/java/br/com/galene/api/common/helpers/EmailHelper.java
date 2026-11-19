package br.com.galene.api.common.helpers;

import java.util.Locale;

public final class EmailHelper {

    private EmailHelper() { }

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
