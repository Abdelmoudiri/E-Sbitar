package org.hospital.sbitari.service;

import org.hospital.sbitari.entity.QueueEntry;

import java.time.LocalDate;
import java.util.List;

public interface QueueService {
    List<QueueEntry> findByDate(LocalDate date);
    QueueEntry create(QueueEntry entry);
    java.util.Optional<QueueEntry> findById(Long id);
    void deleteById(Long id);
}
