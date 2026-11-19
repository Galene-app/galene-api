package br.com.galene.api.shared.domain.exception;

/**
 * Mensagens em pt-BR devolvidas ao cliente no campo {@code message}.
 * Não podem conter linguagem de tratamento ou diagnóstico (Anvisa RDC 657/2022).
 */
public enum ErrorMessage {
    VALIDATION_ERROR("Dados inválidos. Verifique os campos informados."),
    MALFORMED_REQUEST("O corpo da requisição está ausente ou mal formatado."),
    MISSING_PARAMETER("Parâmetro obrigatório não informado."),
    INVALID_PARAMETER_VALUE("Valor inválido para o parâmetro."),
    UNAUTHORIZED("Autenticação necessária. Faça login novamente."),
    ACCESS_DENIED("Você não tem permissão para acessar este recurso."),
    RESOURCE_NOT_FOUND("Recurso não encontrado."),
    DATA_CONFLICT("A operação conflita com dados já existentes."),
    TOO_MANY_ATTEMPTS("Muitas tentativas. Aguarde alguns minutos e tente novamente."),
    METHOD_NOT_ALLOWED("Método HTTP não suportado para este recurso."),
    UNSUPPORTED_MEDIA_TYPE("Tipo de conteúdo não suportado."),
    INTERNAL_ERROR("Ocorreu um erro inesperado. Tente novamente mais tarde.");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
