package com.javid.userservice.util;

import com.javid.userservice.entity.ConfirmationTokenEntity;
import com.javid.userservice.enums.ErrorCode;
import com.javid.userservice.exceptions.TokenHasExpiredException;

import java.time.LocalDateTime;

public class TokenUtil {
    public static void checkTokenExpired(ConfirmationTokenEntity token){
        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenHasExpiredException(ErrorCode.TOKEN_EXPIRED);
        }
    }
}
