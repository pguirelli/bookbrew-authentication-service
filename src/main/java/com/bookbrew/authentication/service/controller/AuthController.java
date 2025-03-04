package com.bookbrew.authentication.service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.bookbrew.authentication.service.dto.ResetPasswordRequestDTO;
import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<User> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequestDTO request) {
        return ResponseEntity.ok(authService.changePassword(userId, request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        response.put("token", authService.forgotPassword(request));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recover-email")
    public ResponseEntity<Map<String, String>> recoverEmail(@Valid @RequestBody EmailRecoveryRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        response.put("email", authService.recoverEmail(request));
        return ResponseEntity.ok(response);
    }
}
