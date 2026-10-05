package com.javid.userservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javid.userservice.dto.ConfirmationResponse;
import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.dto.UserRegisterResponse;
import com.javid.userservice.entity.ConfirmationTokenEntity;
import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.enums.*;
import com.javid.userservice.event.UserCreatedEvent;
import com.javid.userservice.event.UserRegisteredEvent;
import com.javid.userservice.exceptions.UserAlreadyExistsException;
import com.javid.userservice.repository.ConfirmationTokenRepository;
import com.javid.userservice.repository.OutboxRepository;
import com.javid.userservice.repository.UserRepository;
import com.javid.userservice.service.UserService;
import com.javid.userservice.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    @Value("${app.confirmation-url}")
    private String CONFIRM_REGISTRATION_URL;

    @Override
    @Transactional
    @SneakyThrows
    public UserRegisterResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmailAndIsActiveTrue(request.email())) {
            throw new UserAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS_EMAIL, request.email());
        }

        if (userRepository.existsByPhoneNumberAndIsActiveTrue(request.phone())) {
            throw new UserAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS_PHONE_NUMBER, request.phone());
        }

        Optional<UserEntity> pendingEmailUser = userRepository.findByEmail(request.email());
        Optional<UserEntity> pendingPhoneUser = userRepository.findByPhone(request.phone());

        boolean isEmailTokenActive = pendingEmailUser.isPresent() && hasActiveToken(pendingEmailUser.get());
        boolean isPhoneTokenActive = pendingPhoneUser.isPresent() && hasActiveToken(pendingPhoneUser.get());

        if (isEmailTokenActive || isPhoneTokenActive) {
            throw new UserAlreadyExistsException(ErrorCode.PENDING_REGISTRATION_EXISTS, request.email(), request.phone());
        }

        UserEntity user;

        boolean isSameUser = pendingEmailUser.isPresent()
                && pendingPhoneUser.isPresent()
                && Objects.equals(pendingPhoneUser.get().getId(), pendingEmailUser.get().getId());


        if (isSameUser || (pendingPhoneUser.isPresent() ^ pendingEmailUser.isPresent())) {
            user = pendingEmailUser.orElseGet(pendingPhoneUser::get);
            updatePendingUser(user, request);
        } else {
            pendingEmailUser.ifPresent(this::deletePendingUser);
            pendingPhoneUser.ifPresent(this::deletePendingUser);

            userRepository.flush();

            user = createNewUserEntity(request);
        }

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationTokenEntity confirmationToken = ConfirmationTokenEntity.builder()
                .confirmedAt(null)
                .token(token)
                .user(user)
                .type(TokenUsedForType.REGISTRATION)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();


        confirmationTokenRepository.save(confirmationToken);

        String url = CONFIRM_REGISTRATION_URL + token;

        log.info("Confirmation token generated for user: {}, url: {}", user.getEmail(), url);

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                user.getEmail(),
                user.getFirstName(),
                url
        );

        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateType(AggregateType.USER)
                .aggregateId(user.getId().toString())
                .eventType(EventType.USER_REGISTERED)
                .payload(objectMapper.writeValueAsString(event))
                .status(OutboxStatus.PENDING)
                .build();

        outboxRepository.save(outboxEntity);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId().toString()));

        log.info("User registered and Outbox record created for email: {}", user.getEmail());

        return UserRegisterResponse.builder()
                .message("User registered successfully. Please check your email.")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    @Override
    @Transactional
    public ConfirmationResponse confirmRegistration(String token) {
        Optional<ConfirmationTokenEntity> confirmationTokenOpt = confirmationTokenRepository.findByToken(token);

        if (confirmationTokenOpt.isEmpty()) {
            return ConfirmationResponse.builder()
                    .message("User registration already confirmed or invalid token")
                    .build();
        }

        ConfirmationTokenEntity confirmationToken = confirmationTokenOpt.get();

        TokenUtil.checkTokenExpired(confirmationToken);

        UserEntity user = confirmationToken.getUser();

        if (user.isActive()) {
            return ConfirmationResponse.builder()
                    .message("User is already confirmed")
                    .build();
        }

        user.setActive(true);
        user.setStatus(UserStatusEnum.USER);

        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);

        return ConfirmationResponse.builder()
                .message("User confirmed successfully")
                .email(user.getEmail())
                .isConfirmed(true)
                .confirmedAt(LocalDateTime.now())
                .build();
    }


    private boolean hasActiveToken(UserEntity user) {
        if (user.getConfirmationTokens() == null || user.getConfirmationTokens().isEmpty()) {
            return false;
        }

        return user.getConfirmationTokens().stream()
                .anyMatch(token -> token.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    private void updatePendingUser(UserEntity user, UserRegisterRequest request) {
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setStatus(UserStatusEnum.PENDING);
        user.setPhone(request.phone());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        if (user.getConfirmationTokens() != null) {
            user.getConfirmationTokens().removeIf(token -> token.getType() == TokenUsedForType.REGISTRATION);
        }
    }

    private void deletePendingUser(UserEntity user) {
        userRepository.delete(user);
    }

    private UserEntity createNewUserEntity(UserRegisterRequest request) {
        return UserEntity.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .status(UserStatusEnum.PENDING)
                .isActive(false)
                .build();
    }
}