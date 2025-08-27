package com.flab.auth.repository;

import com.flab.auth.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import com.flab.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndStatus(Long id, Status status);
}
