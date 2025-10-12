package org.hospital.sbitari.service.impl;

import org.hospital.sbitari.dao.PatientDao;
import org.hospital.sbitari.dao.impl.PatientDaoImpl;
import org.hospital.sbitari.entity.Patient;
import org.hospital.sbitari.service.PatientService;

import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientDao patientDao = new PatientDaoImpl();

    @Override
    public Optional<Patient> findById(Long id) {
        return patientDao.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return patientDao.findAll();
    }

    @Override
    public Patient create(Patient entity) {
        return patientDao.save(entity);
    }

    @Override
    public Patient update(Patient entity) {
        return patientDao.update(entity);
    }

    @Override
    public void delete(Patient entity) {
        patientDao.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        patientDao.deleteById(id);
    }

    @Override
    public Optional<Patient> findByNumeroSecuriteSociale(String nss) {
        // simple linear search for now; can be replaced by a query in DAO
        return patientDao.findAll().stream().filter(p -> nss != null && nss.equals(p.getNumeroSecuriteSociale())).findFirst();
    }

    @Override
    public List<Patient> findByInfirmierId(Long infirmierId) {
        return patientDao.findAll().stream().filter(p -> p.getInfirmier() != null && infirmierId.equals(p.getInfirmier().getId())).toList();
    }
}
