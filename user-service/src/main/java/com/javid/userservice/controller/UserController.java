package com.javid.userservice.controller;

import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/external/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequest request) {
        return new ResponseEntity<>(
                userService.register(request),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/confirm-registration/{token}")
    public ResponseEntity<String> confirmRegistration(@PathVariable String token) {
        return new ResponseEntity<>(
                userService.confirmRegistration(token),
                HttpStatus.OK
        );
    }
}