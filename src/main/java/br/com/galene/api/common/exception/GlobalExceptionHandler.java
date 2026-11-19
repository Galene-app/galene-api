package br.com.galene.api.common.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.constants.ErrorMessages;
import br.com.galene.api.common.exception.custom.ApiException;
import br.com.galene.api.common.model.ErrorResponse;
import br.com.galene.api.common.model.ErrorResponse.ErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Stream;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        var serverResponse = ErrorResponse.of(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(serverResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        var details = ex.getFieldErrors().stream()
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        return validationError(details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        var details = ex.getParameterValidationResults().stream()
                .flatMap(GlobalExceptionHandler::toDetails)
                .toList();
        return validationError(details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return validationError(List.of(new ErrorDetail(ex.getParameterName(), ErrorMessages.MISSING_PARAMETER)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return validationError(List.of(new ErrorDetail(ex.getName(), ErrorMessages.INVALID_PARAMETER)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody() {
        var serverResponse = ErrorResponse.of(ErrorCodes.MALFORMED_REQUEST, ErrorMessages.MALFORMED_REQUEST);
        return ResponseEntity.badRequest().body(serverResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        var serverResponse = ErrorResponse.of(ErrorCodes.DATA_CONFLICT, ErrorMessages.DATA_CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(serverResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied() {
        var serverResponse = ErrorResponse.of(ErrorCodes.ACCESS_DENIED, ErrorMessages.ACCESS_DENIED);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(serverResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource() {
        var serverResponse = ErrorResponse.of(ErrorCodes.RESOURCE_NOT_FOUND, ErrorMessages.RESOURCE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serverResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported() {
        var serverResponse = ErrorResponse.of(ErrorCodes.METHOD_NOT_ALLOWED, ErrorMessages.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(serverResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported() {
        var serverResponse = ErrorResponse.of(ErrorCodes.UNSUPPORTED_MEDIA_TYPE, ErrorMessages.UNSUPPORTED_MEDIA_TYPE);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(serverResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        var serverResponse = ErrorResponse.of(ErrorCodes.INTERNAL_ERROR, ErrorMessages.INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serverResponse);
    }

    private static ResponseEntity<ErrorResponse> validationError(List<ErrorDetail> details) {
        var serverResponse = new ErrorResponse(ErrorCodes.VALIDATION_ERROR, ErrorMessages.VALIDATION_ERROR, details);
        return ResponseEntity.badRequest().body(serverResponse);
    }

    private static Stream<ErrorDetail> toDetails(ParameterValidationResult result) {
        if (result instanceof ParameterErrors errors)
            return errors.getFieldErrors().stream().map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()));

        String parameterName = result.getMethodParameter().getParameterName();
        return result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(message -> new ErrorDetail(parameterName, message));
    }
}
