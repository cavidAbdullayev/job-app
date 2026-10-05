package com.javid.userservice.repository;

import com.javid.userservice.entity.RefreshTokenEntity;
import com.javid.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("update RefreshTokenEntity r set r.revoked = true where r.user = :user and r.revoked = false")
    void revokeAllUserTokens(UserEntity user);
}