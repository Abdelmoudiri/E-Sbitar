package org.hospital.sbitari.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hospital.sbitari.dao.UserDao;
import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.entity.User;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends JpaGenericDao<User, Long> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager em = em();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            q.setParameter("email", email);
            List<User> list = q.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }
}
