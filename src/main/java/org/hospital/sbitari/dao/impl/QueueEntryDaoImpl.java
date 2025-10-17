package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.dao.QueueEntryDao;
import org.hospital.sbitari.entity.QueueEntry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class QueueEntryDaoImpl extends JpaGenericDao<QueueEntry, Long> implements QueueEntryDao {

    public QueueEntryDaoImpl() { super(QueueEntry.class); }

    @Override
    public List<QueueEntry> findByDate(LocalDate date) {
        EntityManager em = em();
        try {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            String q = "SELECT q FROM QueueEntry q JOIN FETCH q.patient WHERE q.createdAt BETWEEN :start AND :end ORDER BY q.createdAt ASC";
            TypedQuery<QueueEntry> query = em.createQuery(q, QueueEntry.class);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
