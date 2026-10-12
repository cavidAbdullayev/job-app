package com.javid.userservice.mapper.impl;

import com.javid.userservice.dto.UpdateUserRequest;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserMapperImpl implements UserMapper {
    @Override
    public void mapForUpdate(UpdateUserRequest request, UserEntity user) {
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
    }
}
