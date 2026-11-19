package br.com.galene.api.common.constants;

public final class ApiPaths {

    public static final String VERSION = "v1";
    public static final String V1 = "/api/" + VERSION;

    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_LOGOUT = AUTH + "/logout";

    public static final String USERS_ME = V1 + "/users/me";

    private ApiPaths() { }
}
