package com.bookbrew.authentication.service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookbrew.authentication.service.dto.EmailRecoveryRequestDTO;
import com.bookbrew.authentication.service.dto.ForgotPasswordRequestDTO;
import com.bookbrew.authentication.service.dto.LoginRequestDTO;
import com.bookbrew.authentication.service.dto.PasswordChangeRequestDTO;
import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<User> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequestDTO request) {
        User user = authService.changePassword(userId, request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<User> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        User user = authService.forgotPassword(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/recover-email")
    public ResponseEntity<Map<String, String>> recoverEmail(@Valid @RequestBody EmailRecoveryRequestDTO request) {
        String email = authService.recoverEmail(request);
        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        return ResponseEntity.ok(response);
    }
}
