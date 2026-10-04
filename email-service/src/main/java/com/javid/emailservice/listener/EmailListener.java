package com.javid.emailservice.listener;


import com.javid.emailservice.config.RabbitMQConfig;
import com.javid.emailservice.dto.SendEmailDto;
import com.javid.emailservice.enums.EmailSubject;
import com.javid.emailservice.event.UserRegisteredEvent;
import com.javid.emailservice.service.EmailService;
import com.javid.emailservice.service.ProcessedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = RabbitMQConfig.SEND_EMAIL_QUEUE)
public class EmailListener {
    private final EmailService emailService;
    private final ProcessedService processedService;

    @RabbitHandler
    public void handleRegistration(UserRegisteredEvent event) {
        if (processedService.isAlreadyProcessed(event.eventId())) {
            log.warn("Event already processed: {}", event.eventId());
            return;
        }

        SendEmailDto sendEmailDto = new SendEmailDto(
                "Dear " + event.firstName() + ", follow link to complete your registration: " + event.confirmationUrl(),
                event.email(),
                EmailSubject.REGISTRATION_CONFIRMATION
        );

        try {
            emailService.sendEmail(sendEmailDto);
            processedService.saveProcessedEvent(event.eventId());
        } catch (Exception e) {
            log.error("Failed to process email for eventId: {}", event.eventId());
            throw e;
        }
    }
}