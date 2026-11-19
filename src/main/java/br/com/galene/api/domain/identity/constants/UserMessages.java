package br.com.galene.api.domain.identity.constants;

/**
 * Mensagens do contexto identity devolvidas ao cliente (sucesso e regras de negócio).
 */
public final class UserMessages {

    // Sucesso
    public static final String USER_CREATED = "Usuário criado com sucesso";
    public static final String USER_FOUND = "Usuário encontrado";
    public static final String USER_UPDATED = "Usuário atualizado";

    // Regras de negócio
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String EMAIL_ALREADY_REGISTERED = "Login informado já existe";
    public static final String UNDERAGE = "O cadastro é permitido somente para maiores de 18 anos";
    public static final String WEAK_PASSWORD = "A senha deve ter de 8 a 64 caracteres, com letra maiúscula, letra minúscula, "
            + "número e símbolo, sem acentos, espaços ou emojis";

    // Configuração
    public static final String NO_TERM_PUBLISHED = "Nenhum termo foi publicado";

    private UserMessages() { }
}
