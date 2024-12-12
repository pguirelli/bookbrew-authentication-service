package com.bookbrew.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbrew.authentication.service.model.User;
import com.bookbrew.authentication.service.model.UserProfile;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    Optional<User> findByPhone(String phone);

    boolean existsByProfile(UserProfile profile);
}
