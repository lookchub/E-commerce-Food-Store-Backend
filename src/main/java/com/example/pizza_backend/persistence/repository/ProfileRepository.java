package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findFirstByUsernameAndPassword(String username, String password);
    Boolean existsByUsername(String username);
}
