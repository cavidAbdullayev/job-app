package com.javid.reviewms.review.exception.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RemoteErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {

}
