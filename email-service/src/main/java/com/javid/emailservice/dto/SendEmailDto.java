package com.javid.emailservice.dto;

import com.javid.emailservice.enums.EmailSubject;

public record SendEmailDto(
        String message,
        String email,
        EmailSubject subject
) {

}
