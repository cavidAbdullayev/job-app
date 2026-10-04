package com.javid.emailservice.repository;

import com.javid.emailservice.entity.FailedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FailedEventRepository extends JpaRepository<FailedEventEntity, UUID> {

}
