package org.hospital.sbitari.service;

import org.hospital.sbitari.entity.DemandeExpertise;
import java.util.Optional;
import java.util.List;

public interface DemandeExpertiseService {
    DemandeExpertise create(DemandeExpertise d);
    Optional<DemandeExpertise> findById(Long id);
    List<DemandeExpertise> findAll();
    java.util.List<java.util.Map<String,Object>> findAllAsMap();
}
