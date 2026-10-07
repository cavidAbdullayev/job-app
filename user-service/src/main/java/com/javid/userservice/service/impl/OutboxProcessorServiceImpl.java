package com.javid.userservice.service.impl;

import com.javid.userservice.producer.UserServiceProducer;
import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.enums.OutboxStatus;
import com.javid.userservice.repository.OutboxRepository;
import com.javid.userservice.service.OutboxProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessorServiceImpl implements OutboxProcessorService {

    private final OutboxRepository outboxRepository;
    private final UserServiceProducer userServiceProducer;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleOutbox(OutboxEntity outbox) {

        try {
            userServiceProducer.sendEmail(outbox.getEventType().name(), outbox.getPayload());

            outbox.setStatus(OutboxStatus.PROCESSED);
            outbox.setProcessedAt(LocalDateTime.now());
            outboxRepository.save(outbox);

            log.info("Outbox message successfully sent to RabbitMQ queue: {}", outbox.getId());
        } catch (Exception e) {
            log.error("Failed to send Outbox message to RabbitMQ for ID: {}", outbox.getId(), e);

            outbox.setStatus(OutboxStatus.FAILED);
            outbox.setProcessedAt(LocalDateTime.now());
            outboxRepository.save(outbox);
        }

    }
}