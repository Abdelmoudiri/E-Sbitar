package org.hospital.sbitari.dao;

import org.hospital.sbitari.entity.User;

import java.util.Optional;
public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByEmail(String email);
}
