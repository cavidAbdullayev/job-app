package com.javid.userservice.scheduler;

import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.enums.OutboxStatus;
import com.javid.userservice.repository.OutboxRepository;
import com.javid.userservice.service.OutboxProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherScheduler {

    private final OutboxRepository outboxRepository;
private final OutboxProcessorService outboxProcessorService;

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void sendMessage() {
        List<OutboxEntity> outboxes = outboxRepository.findTop10StatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        outboxes.forEach(outbox -> {
            try {
                outboxProcessorService.processSingleOutbox(outbox);
            } catch (Exception e) {
                log.error("Unexpected error while processing outbox batch item ID: {}", outbox.getId(), e);            }
        });

    }
}