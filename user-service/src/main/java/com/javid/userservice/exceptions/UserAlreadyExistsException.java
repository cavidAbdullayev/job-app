package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;

public class UserAlreadyExistsException extends BaseException{
    public UserAlreadyExistsException(ErrorCode errorCode, String... data){
        super(errorCode, (Object) data);
    }
}
