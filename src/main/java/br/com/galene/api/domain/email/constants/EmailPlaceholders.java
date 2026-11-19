package br.com.galene.api.domain.email.constants;

import java.util.regex.Pattern;

public final class EmailPlaceholders {

    public static final String USER_NAME = "{{user_name}}";
    public static final String CODE = "{{code}}";
    public static final String VALIDITY = "{{validity}}";
    public static final String TEMPORARY_PASSWORD = "{{temporary_password}}";

    public static final Pattern ANY_PLACEHOLDER = Pattern.compile("\\{\\{[a-z_]+}}");

    private EmailPlaceholders() { }
}
