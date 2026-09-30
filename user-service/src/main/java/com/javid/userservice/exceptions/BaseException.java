package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    protected BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = args;
    }
}