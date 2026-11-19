package br.com.galene.api.shared.domain.exception;

import org.springframework.http.HttpStatus;

/** Usuário autenticado sem permissão: perfil, consentimento, plano ou idade (403). */
public final class AccessDeniedDomainException extends DomainException {

    public AccessDeniedDomainException(ErrorCode code, ErrorMessage message) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
