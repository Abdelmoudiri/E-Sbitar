package org.hospital.sbitari.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.hospital.sbitari.dao.UserDao;
import org.hospital.sbitari.dao.impl.UserDaoImpl;
import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User create(User user, String rawPassword) {
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPassword(hashed);
        return userDao.save(user);
    }

    @Override
    public boolean authenticate(String email, String rawPassword) {
        Optional<User> u = userDao.findByEmail(email);
        if (u.isEmpty()) {
            System.out.println("[auth] user not found for email=" + email);
            return false;
        }
        String hashed = u.get().getPassword();
        System.out.println("[auth] found user id=" + u.get().getId() + " hashedLen=" + (hashed==null?0:hashed.length()));
        boolean ok = BCrypt.checkpw(rawPassword, hashed);
        System.out.println("[auth] password check result=" + ok);
        return ok;
    }
}
