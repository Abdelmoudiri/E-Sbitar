package org.hospital.sbitari.dao;

import org.hospital.sbitari.entity.Specialist;
import java.util.List;

public interface SpecialistDao extends GenericDao<Specialist, Long> {
    List<Specialist> findBySpecialiteName(String specialiteName);
}
