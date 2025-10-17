package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.dao.SpecialiteDao;
import org.hospital.sbitari.entity.Specialite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class SpecialiteDaoImpl extends JpaGenericDao<Specialite, Long> implements SpecialiteDao {

    public SpecialiteDaoImpl() { super(Specialite.class); }

    @Override
    public Optional<Specialite> findByName(String name) {
        EntityManager em = em();
        try {
            String q = "SELECT s FROM Specialite s WHERE s.nom = :name";
            TypedQuery<Specialite> query = em.createQuery(q, Specialite.class);
            query.setParameter("name", name);
            query.setMaxResults(1);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }
}
