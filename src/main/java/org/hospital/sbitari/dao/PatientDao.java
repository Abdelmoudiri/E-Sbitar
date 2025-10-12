package org.hospital.sbitari.dao;

import org.hospital.sbitari.entity.Patient;
import java.util.Optional;
import java.util.Optional;

public interface PatientDao extends GenericDao<Patient, Long> {

	/**
	 * Find a patient by their numéro de sécurité sociale (SSN).
	 * @param numeroSecuriteSociale the SSN to search for
	 * @return Optional containing the Patient if found
	 */
	Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);

}
