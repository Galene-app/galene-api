package br.com.galene.api.domain.email.constants;

public final class EmailTemplates {

    public static final int TWO_FACTOR_VALIDITY_MINUTES = 5;
    public static final int PASSWORD_RESET_VALIDITY_HOURS = 1;

    public static final String TWO_FACTOR_VALIDITY_LABEL = TWO_FACTOR_VALIDITY_MINUTES + " minutos";
    public static final String PASSWORD_RESET_VALIDITY_LABEL = PASSWORD_RESET_VALIDITY_HOURS + " hora";

    private EmailTemplates() { }
}
