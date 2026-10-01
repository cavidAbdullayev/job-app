package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;

public class TokenNotFoundException extends BaseException{
    public TokenNotFoundException(ErrorCode errorCode){
        super(errorCode);
    }
}
