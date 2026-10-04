package com.javid.emailservice.service.impl;

import com.javid.emailservice.config.RabbitMQConfig;
import com.javid.emailservice.entity.FailedEventEntity;
import com.javid.emailservice.enums.FailedEventStatus;
import com.javid.emailservice.repository.FailedEventRepository;
import com.javid.emailservice.service.FailedEventRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedEventRetryServiceImpl implements FailedEventRetryService {
    private final FailedEventRepository failedEventRepository;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    public void retryFailedEvent(UUID failedEventId){
        FailedEventEntity failedEvent = failedEventRepository.findById(failedEventId)
                .orElseThrow(() -> new RuntimeException("Failed event not found"));

        if(failedEvent.getStatus() != FailedEventStatus.PENDING_RETRY){
            throw new IllegalStateException("Event is already resolved or discarded");
        }

        rabbitTemplate.send(
                RabbitMQConfig.SEND_EMAIL_QUEUE,
                MessageBuilder.withBody(failedEvent.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setHeader("__TypeId__", failedEvent.getEventType())
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)

                        .build()
        );

        failedEvent.setStatus(FailedEventStatus.RESOLVED);
        failedEvent.setRetriedAt(LocalDateTime.now());
        failedEventRepository.save(failedEvent);

        log.info("Failed event successfully re-queued: {}", failedEventId);
    }
}
