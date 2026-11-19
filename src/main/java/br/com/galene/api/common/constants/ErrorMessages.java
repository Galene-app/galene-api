package br.com.galene.api.common.constants;

public final class ErrorMessages {

    public static final String VALIDATION_ERROR = "Dados inválidos ou insuficientes";
    public static final String MISSING_PARAMETER = "Parâmetro obrigatório não informado";
    public static final String INVALID_PARAMETER = "Valor inválido para o parâmetro";
    public static final String MALFORMED_REQUEST = "O corpo da requisição está ausente ou mal formatado";
    public static final String UNAUTHORIZED = "Falha ao se autenticar, faça login novamente";
    public static final String ACCESS_DENIED = "Acesso Negado";
    public static final String RESOURCE_NOT_FOUND = "Recurso não encontrado";
    public static final String DATA_CONFLICT = "A operação conflita com dados já existentes";
    public static final String METHOD_NOT_ALLOWED = "Método HTTP não suportado para este recurso";
    public static final String UNSUPPORTED_MEDIA_TYPE = "Tipo de conteúdo não suportado";
    public static final String INTERNAL_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde";

    private ErrorMessages() { }
}
