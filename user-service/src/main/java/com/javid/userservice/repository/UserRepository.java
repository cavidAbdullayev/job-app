package com.javid.userservice.repository;

import com.javid.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
            select case when count(u) > 0 then true else false end
            from UserEntity u where u.phone = :phoneNumber and u.isActive
""")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("""
select case when count(u) > 0 then true else false end
from UserEntity u where u.email = :email and u.isActive
""")
    boolean existsByEmail(@Param("email") String email);
}
