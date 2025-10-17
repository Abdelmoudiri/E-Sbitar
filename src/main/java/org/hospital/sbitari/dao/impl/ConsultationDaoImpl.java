package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.ConsultationDao;
import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.entity.Consultation;

public class ConsultationDaoImpl extends JpaGenericDao<Consultation, Long> implements ConsultationDao {

    public ConsultationDaoImpl() {
        super(Consultation.class);
    }

    @Override
    public java.util.List<Consultation> findAll() {
        jakarta.persistence.EntityManager em = em();
        try {
            String q = "SELECT c FROM Consultation c LEFT JOIN FETCH c.patient LEFT JOIN FETCH c.generaliste ORDER BY c.createdAt DESC";
            jakarta.persistence.TypedQuery<Consultation> query = em.createQuery(q, Consultation.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public java.util.Optional<Consultation> findById(Long id) {
        jakarta.persistence.EntityManager em = em();
        try {
            String q = "SELECT c FROM Consultation c LEFT JOIN FETCH c.patient LEFT JOIN FETCH c.generaliste WHERE c.id = :id";
            jakarta.persistence.TypedQuery<Consultation> query = em.createQuery(q, Consultation.class);
            query.setParameter("id", id);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

}
