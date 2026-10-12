package com.javid.userservice.mapper;

import com.javid.userservice.dto.UpdateUserRequest;
import com.javid.userservice.entity.UserEntity;

public interface UserMapper {
    void mapForUpdate(UpdateUserRequest request, UserEntity user);
}
