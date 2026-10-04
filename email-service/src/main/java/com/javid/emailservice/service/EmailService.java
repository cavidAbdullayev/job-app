package com.javid.emailservice.service;

import com.javid.emailservice.dto.SendEmailDto;

public interface EmailService {
    void sendEmail(SendEmailDto sendEmailDto);
}
