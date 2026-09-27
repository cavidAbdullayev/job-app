package com.javid.jobms.job.config;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Resilience4jConfig {

    @Bean
    public RegistryEventConsumer<Retry> retryEventConsumer() {
        return new RegistryEventConsumer<Retry>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onRetry(event -> log.warn("Retry cəhdi #{}: {}",
                                event.getNumberOfRetryAttempts(),
                                event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "Bilinməyən xəta"));
            }

            @Override
            public void onEntryRemovedEvent(io.github.resilience4j.core.registry.EntryRemovedEvent<Retry> entryRemoveEvent) {
            }

            @Override
            public void onEntryReplacedEvent(io.github.resilience4j.core.registry.EntryReplacedEvent<Retry> entryReplacedEvent) {
            }
        };
    }
}
