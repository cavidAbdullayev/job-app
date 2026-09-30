package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;

public class InvalidInputException extends BaseException{
    public InvalidInputException(ErrorCode errorCode, String messageDetails) {
        super(errorCode, messageDetails);
    }
}
