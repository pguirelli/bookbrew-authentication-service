package com.bookbrew.authentication.service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookbrew.authentication.service.dto.EmailRecoveryRequestDTO;
import com.bookbrew.authentication.service.dto.ForgotPasswordRequestDTO;
import com.bookbrew.authentication.service.dto.LoginRequestDTO;
import com.bookbrew.authentication.service.dto.PasswordChangeRequestDTO;
import com.bookbrew.authentication.service.dto.ResetPasswordRequestDTO;
import com.bookbrew.authentication.service.exception.BadRequestException;
import com.bookbrew.authentication.service.exception.ResourceNotFoundException;
import com.bookbrew.authentication.service.model.RecoveryToken;
import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.repository.RecoveryTokenRepository;
import com.bookbrew.authentication.service.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecoveryTokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        if (!user.getProfile().getStatus()) {
            throw new BadRequestException("User profile is inactive");
        }

        user.setLastLoginDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User changePassword(Long userId, PasswordChangeRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    public String forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or CPF"));

        if (!user.getCpf().equals(request.getCpf())) {
            throw new BadRequestException("Invalid email or CPF");
        }

        String token = UUID.randomUUID().toString();

        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUser(user);
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(LocalDateTime.now());
        recoveryToken.setExpiresAt(LocalDateTime.now().plusHours(2));
        recoveryToken.setUsed(false);

        tokenRepository.save(recoveryToken);

        return token;
    }

    public User resetPassword(ResetPasswordRequestDTO request) {
        RecoveryToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (token.isUsed()) {
            throw new BadRequestException("Token has already been used");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        token.setUsed(true);

        tokenRepository.save(token);

        return userRepository.save(user);
    }

    public String recoverEmail(EmailRecoveryRequestDTO request) {
        User user;

        if (request.getCpf() != null && !request.getCpf().trim().isEmpty()) {
            user = userRepository.findByCpf(request.getCpf())
                    .orElseThrow(() -> new BadRequestException("CPF not found"));
        } else if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new BadRequestException("Phone number not found"));
        } else {
            throw new BadRequestException("Either CPF or phone number must be provided");
        }

        return user.getEmail();
    }
}
