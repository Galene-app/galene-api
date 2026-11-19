package br.com.galene.api.domain.identity.constants;

public final class AuthMessages {

    public static final String CODE_SENT = "Código de verificação enviado para o seu e-mail";
    public static final String AUTHENTICATED = "Usuário autenticado";
    public static final String SESSION_REFRESHED = "Sessão renovada";
    public static final String LOGGED_OUT = "Sessão encerrada";
    public static final String TEMPORARY_PASSWORD_REQUESTED = "Se o e-mail estiver cadastrado, você receberá uma senha temporária";

    public static final String INVALID_CREDENTIALS = "Credenciais inválidas";
    public static final String INVALID_CODE = "Código inválido ou expirado. Faça login novamente para receber um novo código";
    public static final String INVALID_SESSION = "Sessão inválida ou expirada. Faça login novamente";
    public static final String CODE_NOT_SENT = "Não foi possível enviar o código de verificação. Tente fazer login novamente em instantes";

    private AuthMessages() { }
}
