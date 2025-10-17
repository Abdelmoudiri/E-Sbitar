package org.hospital.sbitari.dao;

import org.hospital.sbitari.entity.Specialite;
import java.util.Optional;

public interface SpecialiteDao extends GenericDao<Specialite, Long> {
    Optional<Specialite> findByName(String name);
}
