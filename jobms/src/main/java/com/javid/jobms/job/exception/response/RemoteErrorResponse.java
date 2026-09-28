package com.javid.jobms.job.exception.response;

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
