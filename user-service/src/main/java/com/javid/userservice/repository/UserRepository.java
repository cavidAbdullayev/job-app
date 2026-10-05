package com.javid.userservice.repository;

import com.javid.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
            select case when count(u) > 0 then true else false end
            from UserEntity u where u.phone = :phoneNumber and u.isActive
""")
    boolean existsByPhoneNumberAndIsActiveTrue(@Param("phoneNumber") String phoneNumber);

    @Query("""

            select case when count(u) > 0 then true else false end
            from UserEntity u where u.email = :email and u.isActive
""")
    boolean existsByEmailAndIsActiveTrue(@Param("email") String email);


    @Query("select u from UserEntity u left join fetch u.confirmationTokens where u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("select u from UserEntity u left join fetch u.confirmationTokens where u.phone = :phone")
    Optional<UserEntity> findByPhone(@Param("phone") String phone);

    @Query("select u from UserEntity u where (u.username = :identifier or u.email = :identifier) and u.isActive")
    Optional<UserEntity> findByUsernameOrEmailActive(@Param("identifier") String identifier);
}