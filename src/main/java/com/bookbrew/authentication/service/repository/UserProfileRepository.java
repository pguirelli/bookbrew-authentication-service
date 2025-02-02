package com.bookbrew.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbrew.authentication.service.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByName(String name);

}
