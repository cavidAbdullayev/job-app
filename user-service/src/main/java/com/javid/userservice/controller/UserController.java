package com.javid.userservice.controller;

import com.javid.userservice.dto.*;
import com.javid.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/external/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return new ResponseEntity<>(
                userService.register(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/confirm-registration/{token}")
    public ResponseEntity<ConfirmationResponse> confirmRegistration(@PathVariable String token) {
        return new ResponseEntity<>(
                userService.confirmRegistration(token),
                HttpStatus.OK
        );
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request
    ) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(userService.updateUser(token, request));
    }
}