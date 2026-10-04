package com.javid.emailservice.entity;

import com.javid.emailservice.enums.FailedEventStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "failed_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailedEventEntity {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false, unique = true)
    String eventId;

    @Column(nullable = false)
    String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    String payload;

    @Column(nullable = false, columnDefinition = "TEXT")
    String errorMessage;

    @Column(columnDefinition = "TEXT")
    String exceptionStackTrace;

    String originalQueue;

    String originalExchange;

    String routingKey;

    Integer retryCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    FailedEventStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    LocalDateTime retriedAt;
}