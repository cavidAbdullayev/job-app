package com.javid.emailservice.service.impl;

import com.javid.emailservice.dto.SendEmailDto;
import com.javid.emailservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(SendEmailDto sendEmailDto) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(sendEmailDto.email());
            helper.setText(sendEmailDto.message());
            helper.setSubject(sendEmailDto.subject().getValue());

            mailSender.send(mimeMessage);

            log.info("Email sent successfully");
        } catch (Exception e) {
            log.error("An error occurred when sending email to {}: {}", sendEmailDto.email(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email to " + sendEmailDto.email(), e);
        }
    }
}