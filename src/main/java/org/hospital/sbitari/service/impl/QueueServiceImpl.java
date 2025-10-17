package org.hospital.sbitari.service.impl;

import org.hospital.sbitari.dao.QueueEntryDao;
import org.hospital.sbitari.dao.impl.QueueEntryDaoImpl;
import org.hospital.sbitari.entity.QueueEntry;
import org.hospital.sbitari.service.QueueService;

import java.time.LocalDate;
import java.util.List;

public class QueueServiceImpl implements QueueService {

    private final QueueEntryDao dao = new QueueEntryDaoImpl();

    @Override
    public List<QueueEntry> findByDate(LocalDate date) {
        return dao.findByDate(date);
    }

    @Override
    public QueueEntry create(QueueEntry entry) {
        return dao.save(entry);
    }

    @Override
    public java.util.Optional<QueueEntry> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
