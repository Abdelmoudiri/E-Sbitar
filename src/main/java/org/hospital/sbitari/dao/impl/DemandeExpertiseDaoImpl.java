package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.DemandeExpertiseDao;
import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.entity.DemandeExpertise;

public class DemandeExpertiseDaoImpl extends JpaGenericDao<DemandeExpertise, Long> implements DemandeExpertiseDao {

    public DemandeExpertiseDaoImpl() { super(DemandeExpertise.class); }

    @Override
    public java.util.List<DemandeExpertise> findAll() {
        jakarta.persistence.EntityManager em = em();
        try {
            String q = "SELECT DISTINCT d FROM DemandeExpertise d LEFT JOIN FETCH d.consultation c LEFT JOIN FETCH c.patient p LEFT JOIN FETCH d.specialist s ORDER BY d.createdAt DESC";
            jakarta.persistence.TypedQuery<DemandeExpertise> query = em.createQuery(q, DemandeExpertise.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
