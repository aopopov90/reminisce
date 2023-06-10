package com.home.reminisce.repository;

import com.home.reminisce.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDisplayName(String displayName);
}
