package com.bookbrew.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbrew.authentication.service.model.RecoveryToken;

public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long> {
    Optional<RecoveryToken> findByToken(String token);
}
