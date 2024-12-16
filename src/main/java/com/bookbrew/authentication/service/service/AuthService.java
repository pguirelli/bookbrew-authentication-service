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
import com.bookbrew.authentication.service.exception.BadRequestException;
import com.bookbrew.authentication.service.exception.ResourceNotFoundException;
import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

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

    public User forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or CPF"));

        if (!user.getCpf().equals(request.getCpf())) {
            throw new BadRequestException("Invalid email or CPF");
        }

        // Generate a new random password
        String newPassword = UUID.randomUUID().toString().substring(0, 8);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdateDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        savedUser.setPassword(newPassword); 
        // Here you would typically send an email with the new password
        // For now, we'll just return the user with the new password
        return savedUser;
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
