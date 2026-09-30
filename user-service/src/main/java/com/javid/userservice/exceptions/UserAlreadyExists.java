package com.javid.userservice.exceptions;

import com.javid.userservice.enums.ErrorCode;

public class UserAlreadyExists extends BaseException{
    public UserAlreadyExists(ErrorCode errorCode, String phoneNumber){
        super(errorCode, phoneNumber);
    }
}
