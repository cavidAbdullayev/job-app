package com.javid.userservice.entity;

import com.javid.userservice.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 25)
    String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    String email;

    @Column(name = "first_name", length = 25)
    String firstName;

    @Column(name = "last_name", length = 25)
    String lastName;

    @Column(name = "phone", length = 13)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    UserStatusEnum status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(nullable = false)
    boolean isActive;

    @OneToMany(mappedBy = "user")
    public List<ConfirmationTokenEntity> confirmationTokens;
}