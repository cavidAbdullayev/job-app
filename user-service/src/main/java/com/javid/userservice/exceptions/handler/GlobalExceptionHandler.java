package com.javid.userservice.exceptions.handler;

import com.javid.userservice.enums.ErrorCode;
import com.javid.userservice.exceptions.BaseException;
import com.javid.userservice.exceptions.response.ErrorResponse;
import com.javid.userservice.util.MessageSourceUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSourceUtils messageSourceUtils;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handlerBaseResponse(BaseException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        String localizedMessage = messageSourceUtils.getLocalizedMessage(errorCode.getMessageKey(), ex.getArgs());

        log.error("Business exception occurred: [{}] - {}", errorCode.getCode(), localizedMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .status(errorCode.getHttpStatus().value())
                .message(localizedMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation failed for request path: {}", request.getRequestURI());

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        String localizedMessage = messageSourceUtils.getLocalizedMessage(errorCode.getMessageKey());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .errorCode(errorCode.getCode())
                .message(localizedMessage)
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        Object[] args = new Object[]{ex.getName(), ex.getValue(), requiredType};

        String localizeMessage = messageSourceUtils.getLocalizedMessage(
                ErrorCode.INVALID_TYPE_CONVERSION.getMessageKey(),
                args
        );

        log.warn("Type mismatch exception: {}", localizeMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.INVALID_TYPE_CONVERSION.getHttpStatus().value())
                .error(ErrorCode.INVALID_TYPE_CONVERSION.getHttpStatus().getReasonPhrase())
                .errorCode(ErrorCode.INVALID_TYPE_CONVERSION.getCode())
                .message(localizeMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception occurred at path [{}]: ", request.getRequestURI(), ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String localizedMessage = messageSourceUtils.getLocalizedMessage(errorCode.getMessageKey());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .errorCode(errorCode.getCode())
                .message(localizedMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}