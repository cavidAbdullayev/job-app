package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;
import lombok.Getter;

@Getter
public class TokenHasExpiredException extends BaseException{
    public TokenHasExpiredException(ErrorCode errorCode){
        super(errorCode);
    }
}
