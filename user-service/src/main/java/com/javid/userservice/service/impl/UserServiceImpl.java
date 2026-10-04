package com.javid.userservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.entity.ConfirmationTokenEntity;
import com.javid.userservice.entity.OutboxEntity;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.enums.ErrorCode;
import com.javid.userservice.enums.OutboxStatus;
import com.javid.userservice.enums.UserStatusEnum;
import com.javid.userservice.event.UserCreatedEvent;
import com.javid.userservice.event.UserRegisteredEvent;
import com.javid.userservice.exceptions.TokenHasExpiredException;
import com.javid.userservice.exceptions.TokenNotFoundException;
import com.javid.userservice.exceptions.UserAlreadyExists;
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
    public String register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_EMAIL, request.email());
        }

        if (userRepository.existsByPhoneNumber(request.phone())) {
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_PHONE_NUMBER, request.phone());
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .status(UserStatusEnum.PENDING)
                .isActive(false)
                .build();

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationTokenEntity confirmationToken = ConfirmationTokenEntity.builder()
                .confirmedAt(null)
                .token(token)
                .user(user)
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
                .aggregateType("USER")
                .aggregateId(user.getId().toString())
                .eventType("USER_REGISTERED")
                .payload(objectMapper.writeValueAsString(event))
                .status(OutboxStatus.PENDING)
                .build();

        outboxRepository.save(outboxEntity);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId().toString()));

        log.info("User registered and Outbox record created for email: {}", user.getEmail());
        return "User registered successfully. Please check your email.";
    }

    @Override
    @Transactional
    public String confirmRegistration(String token) {
        Optional<ConfirmationTokenEntity> confirmationTokenOpt = confirmationTokenRepository.findByToken(token);

        if (confirmationTokenOpt.isEmpty()) {
            return "User registration already confirmed or invalid token";
        }

        ConfirmationTokenEntity confirmationToken = confirmationTokenOpt.get();

        TokenUtil.checkTokenExpired(confirmationToken);

        UserEntity user = confirmationToken.getUser();

        if(user.isActive()){
            return "User is already confirmed";
        }

        user.setActive(true);
        user.setStatus(UserStatusEnum.USER);

        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);

        return "User confirmed successfully";
    }


}