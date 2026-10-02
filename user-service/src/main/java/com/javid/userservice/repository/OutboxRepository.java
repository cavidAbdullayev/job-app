package com.javid.userservice.repository;

import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {
    List<OutboxEntity> findTop10StatusOrderByCreatedAtDesc(OutboxStatus status);
}
