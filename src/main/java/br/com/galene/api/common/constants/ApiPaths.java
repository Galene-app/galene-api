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

    private ApiPaths() { }
}
