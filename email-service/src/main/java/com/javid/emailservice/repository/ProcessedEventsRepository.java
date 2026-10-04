package com.javid.emailservice.repository;

import com.javid.emailservice.entity.ProcessedEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventsRepository extends JpaRepository<ProcessedEvents, UUID> {

}
