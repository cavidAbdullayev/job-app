package com.javid.emailservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID eventId;

    @Column(nullable = false)
    LocalDateTime processedAt;

}
