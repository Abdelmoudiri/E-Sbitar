package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.dao.PatientDao;
import org.hospital.sbitari.dao.QueueEntryDao;
import org.hospital.sbitari.dao.impl.PatientDaoImpl;
import org.hospital.sbitari.dao.impl.QueueEntryDaoImpl;
import org.hospital.sbitari.entity.Infirmier;
import org.hospital.sbitari.entity.Patient;
import org.hospital.sbitari.entity.QueueEntry;
import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.service.PatientService;
import org.hospital.sbitari.service.QueueService;
import org.hospital.sbitari.service.impl.PatientServiceImpl;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ApiPatientServlet", urlPatterns = {"/api/patient"})
public class ApiPatientServlet extends HttpServlet {

    private final PatientDao patientDao = new PatientDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String ssn = req.getParameter("ssn");
            System.out.println("[ApiPatientServlet] doGet called, raw ssn='" + ssn + "'");
            resp.setContentType("application/json;charset=UTF-8");
            if (ssn == null || ssn.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "ssn required"));
                return;
            }
            // normalize ssn: trim and collapse whitespace
            String normalizedSsn = ssn.trim().replaceAll("\\s+", " ");
            System.out.println("[ApiPatientServlet] normalized ssn='" + normalizedSsn + "'");

            try {
                System.out.println("[ApiPatientServlet] calling patientDao.findByNumeroSecuriteSociale()");
                Optional<Patient> p = patientDao.findByNumeroSecuriteSociale(normalizedSsn);
                if (p.isPresent()) {
                    Patient pt = p.get();
                    System.out.println("[ApiPatientServlet] patient found by DAO id=" + pt.getId());
            java.util.Map<String,Object> map = new java.util.HashMap<>();
            map.put("found", true);
            map.put("id", pt.getId() == null ? null : pt.getId().toString());
            map.put("nom", pt.getNom());
            map.put("prenom", pt.getPrenom());
            map.put("email", pt.getEmail());
            map.put("telephone", pt.getTelephone());
            map.put("adresse", pt.getAdresse());
            map.put("numero_securite_sociale", pt.getNumeroSecuriteSociale());
                    // vitals
                    map.put("temperature", pt.getTemperature());
                    map.put("tension", pt.getTension());
                    map.put("frequence_cardiaque", pt.getFrequenceCardiaque());
                    map.put("frequence_respiratoire", pt.getFrequenceRespiratoire());
                    map.put("poids", pt.getPoids());
                    map.put("taille", pt.getTaille());
            mapper.writeValue(resp.getWriter(), map);
                } else {
                    System.out.println("[ApiPatientServlet] patient not found by DAO for ssn='" + normalizedSsn + "'");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    mapper.writeValue(resp.getWriter(), java.util.Map.of("found", false));
                }
            } catch (Exception ex) {
                // If DAO query fails (some JPA/runtime issue), try a safe in-memory fallback via service
                ex.printStackTrace();
                try {
                    PatientService fallback = new PatientServiceImpl();
                    Optional<Patient> pf = fallback.findByNumeroSecuriteSociale(normalizedSsn);
                    if (pf.isPresent()) {
                        System.out.println("[ApiPatientServlet] patient found by fallback service id=" + pf.get().getId());
                        Patient pt = pf.get();
            java.util.Map<String,Object> map = new java.util.HashMap<>();
            map.put("found", true);
            map.put("id", pt.getId() == null ? null : pt.getId().toString());
            map.put("nom", pt.getNom());
            map.put("prenom", pt.getPrenom());
            map.put("email", pt.getEmail());
            map.put("telephone", pt.getTelephone());
            map.put("adresse", pt.getAdresse());
            map.put("numero_securite_sociale", pt.getNumeroSecuriteSociale());
                        // vitals
                        map.put("temperature", pt.getTemperature());
                        map.put("tension", pt.getTension());
                        map.put("frequence_cardiaque", pt.getFrequenceCardiaque());
                        map.put("frequence_respiratoire", pt.getFrequenceRespiratoire());
                        map.put("poids", pt.getPoids());
                        map.put("taille", pt.getTaille());
            map.put("fallback", true);
            mapper.writeValue(resp.getWriter(), map);
                        return;
                    }
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }

                // original fallback failed: return structured error to client
                Throwable root = ex;
                while (root.getCause() != null) root = root.getCause();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String msg = root.getMessage() == null ? ex.getClass().getSimpleName() : root.getMessage();
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "internal", "exception", ex.getClass().getName(), "message", msg));
            }
        } catch (Exception outer) {
            outer.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "internal", "exception", outer.getClass().getName(), "message", outer.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        var node = mapper.readTree(req.getInputStream());
        String nom = node.path("nom").asText(null);
        String prenom = node.path("prenom").asText(null);
        String email = node.path("email").asText(null);
        String password = node.path("password").asText(null);
        String ssn = node.path("numero_securite_sociale").asText(null);
        String telephone = node.path("telephone").asText(null);
        String adresse = node.path("adresse").asText(null);
    String antecedents = node.path("antecedents").asText(null);
    String allergies = node.path("allergies").asText(null);
    String traitements = node.path("traitements_en_cours").asText(null);
    // vitals
        Double temperature = node.has("temperature") && !node.get("temperature").isNull() ? node.path("temperature").asDouble() : null;
        String tension = node.path("tension").asText(null);
        Integer frequenceCardiaque = node.has("frequence_cardiaque") && !node.get("frequence_cardiaque").isNull() ? node.path("frequence_cardiaque").asInt() : null;
        Integer frequenceRespiratoire = node.has("frequence_respiratoire") && !node.get("frequence_respiratoire").isNull() ? node.path("frequence_respiratoire").asInt() : null;
        Double poids = node.has("poids") && !node.get("poids").isNull() ? node.path("poids").asDouble() : null;
        Double taille = node.has("taille") && !node.get("taille").isNull() ? node.path("taille").asDouble() : null;

        if (nom == null || prenom == null || email == null || password == null || ssn == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","missing_fields"));
            return;
        }

        // Server-side validation for vitals (if present)
        String validationError = null;
        if (temperature != null) {
            if (temperature < 30.0 || temperature > 45.0) validationError = "invalid_temperature";
        }
        if (frequenceCardiaque != null) {
            if (frequenceCardiaque < 30 || frequenceCardiaque > 250) validationError = "invalid_frequence_cardiaque";
        }
        if (frequenceRespiratoire != null) {
            if (frequenceRespiratoire < 5 || frequenceRespiratoire > 60) validationError = "invalid_frequence_respiratoire";
        }
        if (poids != null) {
            if (poids <= 0 || poids > 500) validationError = "invalid_poids";
        }
        if (taille != null) {
            if (taille <= 0 || taille > 3.0) validationError = "invalid_taille";
        }
        if (validationError != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error", validationError));
            return;
        }

        Patient patient = new Patient(nom, prenom, email, password, null, ssn);
        patient.setTelephone(telephone);
        patient.setAdresse(adresse);
    // medical data
    if (antecedents != null && !antecedents.isBlank()) patient.setAntecedents(antecedents);
    if (allergies != null && !allergies.isBlank()) patient.setAllergies(allergies);
    if (traitements != null && !traitements.isBlank()) patient.setTraitementsEnCours(traitements);
    // set vitals if present
    if (temperature != null) patient.setTemperature(temperature);
    if (tension != null && !tension.isBlank()) patient.setTension(tension);
    if (frequenceCardiaque != null) patient.setFrequenceCardiaque(frequenceCardiaque);
    if (frequenceRespiratoire != null) patient.setFrequenceRespiratoire(frequenceRespiratoire);
    if (poids != null) patient.setPoids(poids);
    if (taille != null) patient.setTaille(taille);

        var session = req.getSession(false);
        if (session != null && session.getAttribute("user") instanceof User) {
            User u = (User) session.getAttribute("user");
            if (u instanceof Infirmier) {
                patient.setInfirmier((Infirmier) u);
            }
        }

        try {
            patient = patientDao.save(patient);

            // create a queue entry for this patient so they appear in the generaliste queue
            try {
                QueueEntryDaoImpl qdao = new QueueEntryDaoImpl();
                QueueEntry queueEntry = new QueueEntry(patient);
                qdao.save(queueEntry);
            } catch (Exception qe) {
                qe.printStackTrace();
            }

            mapper.writeValue(resp.getWriter(), java.util.Map.of("success", true, "id", patient.getId().toString()));
        } catch (Exception ex) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (msg.contains("constraint") || msg.contains("duplicate") || msg.contains("unique") || msg.contains("integrity")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "ssn_exists", "message", "numero_securite_sociale already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "internal", "message", "unable to save patient"));
            }
        }
    }
}
