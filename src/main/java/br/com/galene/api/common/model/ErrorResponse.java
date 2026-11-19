package br.com.galene.api.common.model;

import java.util.List;

public record ErrorResponse(String code, String message, List<ErrorDetail> details) {

    public ErrorResponse {
        details = details == null ? List.of() : List.copyOf(details);
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, List.of());
    }

    public record ErrorDetail(String field, String message) { }
}
