package br.com.galene.api.shared.adapters.in.web;

import java.util.List;

import br.com.galene.api.shared.domain.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Formato padrão de erro")
public record ErrorResponse(String code, String message, List<ErrorDetail> details) {

    public ErrorResponse {
        details = details == null ? List.of() : List.copyOf(details);
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code.name(), message, List.of());
    }

    public static ErrorResponse of(ErrorCode code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(code.name(), message, details);
    }

    @Schema(name = "ErrorDetail", description = "Erro associado a um campo ou parâmetro")
    public record ErrorDetail(String field, String message) { }
}
