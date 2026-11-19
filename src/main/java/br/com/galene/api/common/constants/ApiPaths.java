package br.com.galene.api.common.constants;

public final class ApiPaths {

    public static final String VERSION = "v1";
    public static final String V1 = "/api/" + VERSION;

    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_TWO_FACTOR_VERIFY = "/2fa/verify";
    public static final String AUTH_REFRESH = "/refresh";
    public static final String AUTH_LOGOUT_PATH = "/logout";
    public static final String AUTH_LOGOUT = AUTH + AUTH_LOGOUT_PATH;
    public static final String AUTH_PASSWORD_FORGOT = "/password/forgot";

    public static final String USERS_ME = V1 + "/users/me";
    public static final String USERS_ME_PASSWORD = "/password";
    public static final String USERS_ME_CONSENTS = USERS_ME + "/consents";
    public static final String CONSENT_TERM_TYPE = "/{termType}";
    public static final String USERS_ME_PERMISSIONS = USERS_ME + "/permissions";
    public static final String USERS_ME_QUESTIONNAIRE = USERS_ME + "/questionnaire";
    public static final String USERS_ME_PROFILE_SOUND = USERS_ME + "/profile-sound";

    public static final String QUESTIONNAIRE = V1 + "/questionnaire";

    public static final String USERS_ME_NOISE_READINGS = USERS_ME + "/noise-readings";
    public static final String NOISE_LEVELS = V1 + "/noise-levels";

    public static final String USERS_ME_NOTIFICATIONS = USERS_ME + "/notifications";
    public static final String NOTIFICATION_ID = "/{notificationId}";

    public static final String TERMS = V1 + "/terms";

    private ApiPaths() { }
}
