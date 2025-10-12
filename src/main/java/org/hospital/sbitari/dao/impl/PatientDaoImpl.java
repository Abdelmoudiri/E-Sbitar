package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.dao.PatientDao;
import org.hospital.sbitari.entity.Patient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class PatientDaoImpl extends JpaGenericDao<Patient, Long> implements PatientDao {
    public PatientDaoImpl() {
        super(Patient.class);
    }

    @Override
    public Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale) {
        EntityManager em = em();
        try {
            String q = "SELECT p FROM Patient p WHERE p.numeroSecuriteSociale = :ssn";
            TypedQuery<Patient> query = em.createQuery(q, Patient.class);
            query.setParameter("ssn", numeroSecuriteSociale);
            query.setMaxResults(1);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }
}
