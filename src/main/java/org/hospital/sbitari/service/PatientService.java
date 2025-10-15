package org.hospital.sbitari.service;

import org.hospital.sbitari.entity.Patient;

import java.util.List;
import java.util.Optional;
import java.util.Optional;
import java.util.List;

public interface PatientService extends GenericService<Patient, Long> {
    Optional<Patient> findByNumeroSecuriteSociale(String nss);
    List<Patient> findByInfirmierId(Long infirmierId);
}
