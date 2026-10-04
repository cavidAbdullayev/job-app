package com.javid.emailservice.service.impl;

import com.javid.emailservice.entity.ProcessedEvents;
import com.javid.emailservice.repository.ProcessedEventsRepository;
import com.javid.emailservice.service.ProcessedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessedServiceImpl implements ProcessedService {
    private final ProcessedEventsRepository processedEventsRepository;

    @Transactional
    public void saveProcessedEvent(UUID eventId) {
        processedEventsRepository.save(new ProcessedEvents(eventId, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyProcessed(UUID eventId) {
        return processedEventsRepository.existsById(eventId);
    }

}