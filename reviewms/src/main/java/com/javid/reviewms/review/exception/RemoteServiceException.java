package com.javid.reviewms.review.exception;

import lombok.Getter;

@Getter
public class RemoteServiceException extends RuntimeException {
    private final int status;
    private final String remoteMessage;

    public RemoteServiceException(int status, String remoteMessage) {
        super(remoteMessage);
        this.status = status;
        this.remoteMessage = remoteMessage;
    }
}