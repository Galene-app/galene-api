package br.com.galene.api.common.constants;

public final class ErrorCodes {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String DATA_CONFLICT = "DATA_CONFLICT";
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String EMAIL_ALREADY_REGISTERED = "EMAIL_ALREADY_REGISTERED";
    public static final String UNDERAGE = "UNDERAGE";
    public static final String WEAK_PASSWORD = "WEAK_PASSWORD";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_TWO_FACTOR_CODE = "INVALID_TWO_FACTOR_CODE";
    public static final String TWO_FACTOR_CODE_NOT_SENT = "TWO_FACTOR_CODE_NOT_SENT";
    public static final String INVALID_SESSION = "INVALID_SESSION";
    public static final String INVALID_CURRENT_PASSWORD = "INVALID_CURRENT_PASSWORD";
    public static final String SAME_PASSWORD = "SAME_PASSWORD";
    public static final String TERM_NOT_FOUND = "TERM_NOT_FOUND";
    public static final String CONSENT_NOT_REVOCABLE = "CONSENT_NOT_REVOCABLE";
    public static final String CONSENT_REQUIRED = "CONSENT_REQUIRED";
    public static final String MICROPHONE_REQUIRED = "MICROPHONE_REQUIRED";

    private ErrorCodes() { }
}
