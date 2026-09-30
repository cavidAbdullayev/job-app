package com.javid.userservice.service.impl;

import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.enums.ErrorCode;
import com.javid.userservice.enums.UserStatusEnum;
import com.javid.userservice.exceptions.UserAlreadyExists;
import com.javid.userservice.repository.UserRepository;
import com.javid.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public String register(UserRegisterRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_PHONE_NUMBER, request.phone());
        }

        if(userRepository.existsByPhoneNumber(request.phone())){
            throw new UserAlreadyExists(ErrorCode.USER_ALREADY_EXISTS_EMAIL, request.email());
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .phone(request.phone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .status(UserStatusEnum.USER)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

}