package com.javid.userservice.repository;

import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {
    @Query("select o from OutboxEntity o where o.status = :status order by o.createdAt asc limit 10")
    List<OutboxEntity> findTop10StatusOrderByCreatedAtAsc(@Param("status") OutboxStatus status);

}
