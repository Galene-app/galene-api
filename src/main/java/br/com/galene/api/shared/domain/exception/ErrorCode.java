package br.com.galene.api.shared.domain.exception;

/**
 * Códigos de erro estáveis devolvidos no campo {@code code} do contrato de erro.
 * Novos códigos são adicionados pelos incrementos que os utilizam.
 */
public enum ErrorCode {
    VALIDATION_ERROR,
    MALFORMED_REQUEST,
    UNAUTHORIZED,
    ACCESS_DENIED,
    RESOURCE_NOT_FOUND,
    DATA_CONFLICT,
    BUSINESS_RULE_VIOLATION,
    TOO_MANY_ATTEMPTS,
    METHOD_NOT_ALLOWED,
    UNSUPPORTED_MEDIA_TYPE,
    INTERNAL_ERROR
}
