package org.hospital.sbitari.service.impl;

import org.hospital.sbitari.dao.DemandeExpertiseDao;
import org.hospital.sbitari.dao.impl.DemandeExpertiseDaoImpl;
import org.hospital.sbitari.entity.DemandeExpertise;
import org.hospital.sbitari.service.DemandeExpertiseService;

import java.util.List;
import java.util.Optional;

public class DemandeExpertiseServiceImpl implements DemandeExpertiseService {

    private final DemandeExpertiseDao dao = new DemandeExpertiseDaoImpl();

    @Override
    public DemandeExpertise create(DemandeExpertise d) {
        return dao.save(d);
    }

    @Override
    public Optional<DemandeExpertise> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public List<DemandeExpertise> findAll() {
        return dao.findAll();
    }

    @Override
    public java.util.List<java.util.Map<String,Object>> findAllAsMap() {
        java.util.List<DemandeExpertise> list = dao.findAll();
        java.util.List<java.util.Map<String,Object>> out = new java.util.ArrayList<>();
        for (DemandeExpertise d : list) {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", d.getId());
            m.put("consultationId", d.getConsultation() != null ? d.getConsultation().getId() : null);
            if (d.getConsultation() != null && d.getConsultation().getPatient() != null) {
                m.put("patientId", d.getConsultation().getPatient().getId());
                m.put("patientNom", d.getConsultation().getPatient().getNom());
                m.put("patientPrenom", d.getConsultation().getPatient().getPrenom());
            }
            m.put("specialite", d.getSpecialite());
            m.put("priority", d.getPriority() != null ? d.getPriority().name() : null);
            m.put("status", d.getStatus() != null ? d.getStatus().name() : null);
            m.put("question", d.getQuestion());
            m.put("createdAt", d.getCreatedAt() != null ? d.getCreatedAt().toString() : null);
            m.put("reponse", d.getReponse());
            out.add(m);
        }
        return out;
    }
}
