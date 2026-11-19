package br.com.galene.api.common.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base das exceções de regra de negócio. Cada exceção de domínio fixa o status HTTP e o código
 * devolvidos no formato de erro {@code { code, message, details }}.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
