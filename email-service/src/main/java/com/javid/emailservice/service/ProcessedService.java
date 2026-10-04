package com.javid.emailservice.service;

import com.javid.emailservice.entity.ProcessedEvents;

import java.util.UUID;

public interface ProcessedService {
    void saveProcessedEvent(UUID eventId);
    boolean isAlreadyProcessed(UUID eventId);

}
