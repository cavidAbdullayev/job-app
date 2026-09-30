package com.javid.userservice.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_ALREADY_EXISTS_PHONE_NUMBER("USER_ALREADY_EXISTS_PHONE_NUMBER", "error.user.already-exists-phoneNumber", HttpStatus.CONFLICT),
    USER_ALREADY_EXISTS_EMAIL("USER_ALREADY_EXISTS_EMAIL", "error.user.already-exists-email", HttpStatus.CONFLICT),
    VALIDATION_FAILED("VALIDATION_FAILED", "error.validation.failed", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "error.internal.server", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_CONVERSION("INVALID_TYPE_CONVERSION","error.invalid.type-conversion",HttpStatus.BAD_REQUEST),
    INVALID_INPUT("INVALID_INPUT", "error.invalid.input", HttpStatus.BAD_REQUEST);



    private final String code;
    private final String messageKey;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String messageKey, HttpStatus httpStatus) {
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

}
