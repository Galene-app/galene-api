package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Credenciais, código ou token inválidos/expirados (401). */
public final class AuthenticationFailedException extends DomainException {

    public AuthenticationFailedException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
