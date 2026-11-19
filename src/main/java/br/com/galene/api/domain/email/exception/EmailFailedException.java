package br.com.galene.api.domain.email.exception;

/**
 * Falha de infraestrutura no envio (não é regra de negócio). O envio é assíncrono,
 * então ela nunca chega ao cliente: o usuário usa o reenvio.
 */
public class EmailFailedException extends RuntimeException {
    public EmailFailedException(String message) {
        super(message);
    }

    public EmailFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
