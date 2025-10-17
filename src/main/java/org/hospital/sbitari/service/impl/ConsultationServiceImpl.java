package org.hospital.sbitari.service.impl;

import org.hospital.sbitari.dao.ConsultationDao;
import org.hospital.sbitari.dao.impl.ConsultationDaoImpl;
import org.hospital.sbitari.entity.Consultation;
import org.hospital.sbitari.service.ConsultationService;

import java.util.List;
import java.util.Optional;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationDao consultationDao = new ConsultationDaoImpl();

    @Override
    public Optional<Consultation> findById(Long id) {
        return consultationDao.findById(id);
    }

    @Override
    public List<Consultation> findAll() {
        return consultationDao.findAll();
    }

    @Override
    public Consultation create(Consultation entity) {
        return consultationDao.save(entity);
    }

    @Override
    public Consultation update(Consultation entity) {
        return consultationDao.update(entity);
    }

    @Override
    public void delete(Consultation entity) {
        consultationDao.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        consultationDao.deleteById(id);
    }
}
