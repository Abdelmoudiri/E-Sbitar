package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.SpecialistDao;
import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.entity.Specialist;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SpecialistDaoImpl extends JpaGenericDao<Specialist, Long> implements SpecialistDao {

    public SpecialistDaoImpl() { super(Specialist.class); }

    @Override
    public List<Specialist> findBySpecialiteName(String specialiteName) {
        EntityManager em = em();
        try {
            String q = "SELECT s FROM Specialist s JOIN s.specialite sp WHERE sp.nom = :name";
            TypedQuery<Specialist> query = em.createQuery(q, Specialist.class);
            query.setParameter("name", specialiteName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
