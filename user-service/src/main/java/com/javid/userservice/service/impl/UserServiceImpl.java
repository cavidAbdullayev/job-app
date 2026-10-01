package com.javid.userservice.service.impl;

import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.entity.ConfirmationTokenEntity;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.enums.ErrorCode;
import com.javid.userservice.enums.UserStatusEnum;
import com.javid.userservice.exceptions.TokenHasExpiredException;
import com.javid.userservice.exceptions.TokenNotFoundException;
import com.javid.userservice.exceptions.UserAlreadyExists;
import com.javid.userservice.listener.UserCacheListener;
import com.javid.userservice.repository.ConfirmationTokenRepository;
import com.javid.userservice.repository.UserRepository;
import com.javid.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserCacheListener userCacheListener;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.confirmation-url}")
    private String CONFIRM_REGISTRATION_URL;

    @Override
    @Transactional
    public String register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_EMAIL, request.phone());
        }

        if (userRepository.existsByPhoneNumber(request.phone())) {
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_PHONE_NUMBER, request.email());
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
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

        String message = "Follow this link for activate your account: " + url;

        userCacheListener.evictCache(null);
        //emailServiceListener.sendMail(message);

        //TODO: send email email-service
        return "User registered successfully";
    }

    @Override
    public String confirmRegistration(String token) {
        Optional<ConfirmationTokenEntity> confirmationTokenOpt = confirmationTokenRepository.findByToken(token);

        if (confirmationTokenOpt.isEmpty()) {
            throw new TokenNotFoundException(ErrorCode.TOKEN_EXPIRED);
        }

        ConfirmationTokenEntity confirmationToken = confirmationTokenOpt.get();

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenHasExpiredException(ErrorCode.TOKEN_EXPIRED);
        }

        UserEntity user = confirmationToken.getUser();

        user.setActive(true);
        user.setStatus(UserStatusEnum.USER);

        userRepository.save(user);

        confirmationTokenRepository.delete(confirmationToken);

        return "User confirmed successfully";
    }

}