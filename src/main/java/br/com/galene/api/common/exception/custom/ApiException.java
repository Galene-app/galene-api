package br.com.galene.api.common.exception.custom;

import br.com.galene.api.common.model.ErrorResponse.ErrorDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;


@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<ErrorDetail> details;

    protected ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, List.of());
    }

    protected ApiException(HttpStatus status, String code, String message, List<ErrorDetail> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = List.copyOf(details);
    }
}
