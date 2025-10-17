package org.hospital.sbitari.dao;

import org.hospital.sbitari.entity.QueueEntry;

import java.time.LocalDate;
import java.util.List;

public interface QueueEntryDao extends GenericDao<QueueEntry, Long> {
    List<QueueEntry> findByDate(LocalDate date);
}
