package br.com.galene.api.shared.adapters.in.web;

import java.util.List;
import java.util.stream.Stream;

import br.com.galene.api.shared.adapters.in.web.ErrorResponse.ErrorDetail;
import br.com.galene.api.shared.domain.exception.DomainException;
import br.com.galene.api.shared.domain.exception.ErrorCode;
import br.com.galene.api.shared.domain.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex) {
        return build(ex.getStatus(), ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = Stream.concat(
                ex.getBindingResult().getFieldErrors().stream().map(GlobalExceptionHandler::toDetail),
                ex.getBindingResult().getGlobalErrors().stream()
                        .map(error -> new ErrorDetail(error.getObjectName(), error.getDefaultMessage())))
                .toList();
        return validationError(details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        List<ErrorDetail> details = ex.getParameterValidationResults().stream()
                .flatMap(GlobalExceptionHandler::toDetails)
                .toList();
        return validationError(details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return validationError(List.of(
                new ErrorDetail(ex.getParameterName(), ErrorMessage.MISSING_PARAMETER.text())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return validationError(List.of(
                new ErrorDetail(ex.getName(), ErrorMessage.INVALID_PARAMETER_VALUE.text())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST,
                ErrorResponse.of(ErrorCode.MALFORMED_REQUEST, ErrorMessage.MALFORMED_REQUEST.text()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                ErrorResponse.of(ErrorCode.DATA_CONFLICT, ErrorMessage.DATA_CONFLICT.text()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN,
                ErrorResponse.of(ErrorCode.ACCESS_DENIED, ErrorMessage.ACCESS_DENIED.text()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND,
                ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, ErrorMessage.RESOURCE_NOT_FOUND.text()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED,
                ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, ErrorMessage.METHOD_NOT_ALLOWED.text()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ErrorResponse.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ErrorMessage.UNSUPPORTED_MEDIA_TYPE.text()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorMessage.INTERNAL_ERROR.text()));
    }

    private static ResponseEntity<ErrorResponse> validationError(List<ErrorDetail> details) {
        return build(HttpStatus.BAD_REQUEST,
                ErrorResponse.of(ErrorCode.VALIDATION_ERROR, ErrorMessage.VALIDATION_ERROR.text(), details));
    }

    private static ResponseEntity<ErrorResponse> build(HttpStatus status, ErrorResponse body) {
        return ResponseEntity.status(status).body(body);
    }

    private static ErrorDetail toDetail(FieldError error) {
        return new ErrorDetail(error.getField(), error.getDefaultMessage());
    }

    private static Stream<ErrorDetail> toDetails(ParameterValidationResult result) {
        if (result instanceof ParameterErrors errors) {
            return errors.getFieldErrors().stream().map(GlobalExceptionHandler::toDetail);
        }
        String parameterName = result.getMethodParameter().getParameterName();
        return result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(message -> new ErrorDetail(parameterName, message));
    }
}
