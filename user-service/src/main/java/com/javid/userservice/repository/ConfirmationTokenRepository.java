package com.javid.userservice.repository;

import com.javid.userservice.entity.ConfirmationTokenEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {

    @Query("select t from ConfirmationTokenEntity t where t.token = :token")
    Optional<ConfirmationTokenEntity> findByToken(@Param("token") String token);
}
