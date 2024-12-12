package com.bookbrew.authentication.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookbrew.authentication.service.exception.BadRequestException;
import com.bookbrew.authentication.service.exception.DuplicateNameException;
import com.bookbrew.authentication.service.exception.ResourceNotFoundException;
import com.bookbrew.authentication.service.model.UserProfile;
import com.bookbrew.authentication.service.repository.UserProfileRepository;
import com.bookbrew.authentication.service.repository.UserRepository;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserProfile> getAllUserProfiles() {
        List<UserProfile> profiles = userProfileRepository.findAll();
        if (profiles.isEmpty()) {
            throw new ResourceNotFoundException("No user profiles found");
        }
        return profiles;
    }

    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));
    }

    public UserProfile createUserProfile(UserProfile userProfile) {
        validateUserProfile(userProfile);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserProfile(Long id, UserProfile userProfileDetails) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));

        validateUserProfile(userProfileDetails);

        userProfile.setName(userProfileDetails.getName());
        userProfile.setStatus(userProfileDetails.getStatus());

        return userProfileRepository.save(userProfile);
    }

    public void deleteUserProfile(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));

        if (userRepository.existsByProfile(userProfile)) {
            throw new BadRequestException(
                    "Cannot delete profile with ID " + id + ". There are users associated with this profile");
        }

        userProfileRepository.delete(userProfile);
    }

    private void validateUserProfile(UserProfile userProfile) {
        if (userProfile.getName() == null || userProfile.getName().trim().isEmpty()) {
            throw new BadRequestException("User profile name cannot be empty");
        }
        if (userProfileRepository.findByName(userProfile.getName()).isPresent()) {
            throw new DuplicateNameException("Name '" + userProfile.getName() + "' is already in use");
        }
    }
}
