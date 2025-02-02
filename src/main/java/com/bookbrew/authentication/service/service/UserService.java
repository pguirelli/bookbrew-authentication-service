package com.bookbrew.authentication.service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookbrew.authentication.service.dto.UserDTO;
import com.bookbrew.authentication.service.exception.BadRequestException;
import com.bookbrew.authentication.service.exception.ResourceNotFoundException;
import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.model.UserProfile;
import com.bookbrew.authentication.service.repository.UserProfileRepository;
import com.bookbrew.authentication.service.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        if (userRepository.findAll().isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }

        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return convertToDTO(user);
    }

    public UserDTO createUser(User user) {
        validateUserProfile(user.getProfile());
        validateUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationDate(LocalDateTime.now());

        userRepository.save(user);

        return convertToDTO(user);
    }

    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        validateUserProfile(userDetails.getProfile());
        validateUserUpdate(user, userDetails);

        if (userDetails.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        user.setName(userDetails.getName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setUpdateDate(LocalDateTime.now());
        user.setStatus(userDetails.getStatus());

        userRepository.save(user);

        return convertToDTO(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void validateUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BadRequestException("Email '" + user.getEmail() + "' is already in use.");
        }
        if (userRepository.findByCpf(user.getCpf()).isPresent()) {
            throw new BadRequestException("CPF '" + user.getCpf() + "' is already in use.");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new BadRequestException("Phone '" + user.getPhone() + "' is already in use.");
        }
        if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            throw new BadRequestException("Password must be between 6 and 20 characters");
        }
    }

    private void validateUserUpdate(User user, User modifiedUser) {
        if (!user.getEmail().equals(modifiedUser.getEmail())) {
            if (userRepository.findByEmail(modifiedUser.getEmail()).isPresent()) {
                throw new BadRequestException("Email '" + modifiedUser.getEmail() + "' is already in use.");
            }
        }
        if (!user.getPhone().equals(modifiedUser.getPhone())) {
            if (userRepository.findByPhone(modifiedUser.getPhone()).isPresent()) {
                throw new BadRequestException("Phone '" + modifiedUser.getPhone() + "' is already in use.");
            }
        }
        if (!user.getPhone().equals(modifiedUser.getPhone())) {
            if (modifiedUser.getPassword().length() < 6 || modifiedUser.getPassword().length() > 20) {
                throw new BadRequestException("Password must be between 6 and 20 characters");
            }
        }
    }

    private void validateUserProfile(UserProfile profile) {
        if (profile == null || profile.getId() == null) {
            throw new BadRequestException("User profile is required");
        }

        if (!userProfileRepository.findById(profile.getId())
                .orElseThrow(() -> new BadRequestException("Profile not found with id: " + profile.getId()))
                .getStatus()) {
            throw new BadRequestException("Cannot associate user with inactive profile (ID: " + profile.getId() + ")");
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCpf(user.getCpf());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setStatus(user.getStatus());
        dto.setIdProfile(user.getProfile().getId());
        dto.setCreationDate(user.getCreationDate());
        dto.setUpdateDate(user.getUpdateDate());
        dto.setLastLoginDate(user.getLastLoginDate());
        dto.setPasswordUpdateDate(user.getPasswordUpdateDate());

        return dto;
    }
}
