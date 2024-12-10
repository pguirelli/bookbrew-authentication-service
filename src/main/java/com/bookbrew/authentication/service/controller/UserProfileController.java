package com.bookbrew.authentication.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookbrew.authentication.service.exception.BadRequestException;
import com.bookbrew.authentication.service.exception.DuplicateNameException;
import com.bookbrew.authentication.service.exception.ResourceNotFoundException;
import com.bookbrew.authentication.service.model.UserProfile;
import com.bookbrew.authentication.service.repository.UserProfileRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping
    public List<UserProfile> getAllUserProfiles() {
        List<UserProfile> profiles = userProfileRepository.findAll();
        if (profiles.isEmpty()) {
            throw new ResourceNotFoundException("No user profiles found");
        }
        return profiles;
    }

    @PostMapping
    public UserProfile createUserProfile(@Valid @RequestBody UserProfile userProfile) {
        if (userProfile.getName() == null || userProfile.getName().trim().isEmpty()) {
            throw new BadRequestException("User profile name cannot be empty");
        }
        checkForDuplicateName(userProfile.getName());
        return userProfileRepository.save(userProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/{id}")
    public UserProfile updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfile userProfileDetails) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));

        if (userProfileDetails.getName() == null || userProfileDetails.getName().trim().isEmpty()) {
            throw new BadRequestException("User profile name cannot be empty");
        }

        checkForDuplicateName(userProfileDetails.getName());

        userProfile.setName(userProfileDetails.getName());
        userProfile.setStatus(userProfileDetails.getStatus());

        return userProfileRepository.save(userProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserProfile(@PathVariable Long id) {
        return userProfileRepository.findById(id)
                .map(userProfile -> {
                    userProfileRepository.delete(userProfile);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));
    }

    private void checkForDuplicateName(String name) {
        if (userProfileRepository.findByName(name).isPresent()) {
            throw new DuplicateNameException("Name '" + name + "' is already in use");
        }
    }
}
