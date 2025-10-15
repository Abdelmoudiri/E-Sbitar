package org.hospital.sbitari.service;

import org.hospital.sbitari.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User create(User user, String rawPassword);
    boolean authenticate(String email, String rawPassword);
}
