package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.Patient;
import org.hospital.sbitari.entity.QueueEntry;
import org.hospital.sbitari.service.PatientService;
import org.hospital.sbitari.service.impl.PatientServiceImpl;
import org.hospital.sbitari.dao.QueueEntryDao;
import org.hospital.sbitari.dao.impl.QueueEntryDaoImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ApiPatientsListServlet", urlPatterns = {"/api/patients"})
public class ApiPatientsListServlet extends HttpServlet {

    private final PatientService patientService = new PatientServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String dateParam = req.getParameter("date"); // YYYY-MM-DD optional
        String q = req.getParameter("q"); // search query optional

        java.util.List<java.util.Map<String,Object>> list = new java.util.ArrayList<>();

        try {
            if (dateParam != null && !dateParam.isBlank()) {
                java.time.LocalDate date = java.time.LocalDate.parse(dateParam);
                QueueEntryDao qdao = new QueueEntryDaoImpl();
                java.util.List<QueueEntry> entries = qdao.findByDate(date);
                for (QueueEntry qe : entries) {
                    Patient p = qe.getPatient();
                    if (p == null) continue;
                    String nom = java.util.Objects.toString(p.getNom(), "").trim();
                    String prenom = java.util.Objects.toString(p.getPrenom(), "").trim();
                    String nss = java.util.Objects.toString(p.getNumeroSecuriteSociale(), "").trim();
                    String telephone = java.util.Objects.toString(p.getTelephone(), "").trim();
                    if (q != null && !q.isBlank()) {
                        String ql = q.toLowerCase();
                        if (!(nom.toLowerCase().contains(ql) || prenom.toLowerCase().contains(ql) || nss.toLowerCase().contains(ql))) continue;
                    }
                    var m = new java.util.HashMap<String,Object>();
                    m.put("id", p.getId() == null ? null : p.getId().toString());
                    m.put("nom", nom.isEmpty() ? java.util.Objects.toString(p.getEmail(),"") : nom);
                    m.put("prenom", prenom.isEmpty() ? "" : prenom);
                    m.put("telephone", telephone);
                    m.put("numero_securite_sociale", nss);
                    m.put("createdAt", qe.getCreatedAt() != null ? qe.getCreatedAt().toString() : null);
                    list.add(m);
                }
            } else {
                // fallback: list all patients
                List<Patient> all = patientService.findAll();
                for (Patient p : all) {
                    String nom = java.util.Objects.toString(p.getNom(), "").trim();
                    String prenom = java.util.Objects.toString(p.getPrenom(), "").trim();
                    String telephone = java.util.Objects.toString(p.getTelephone(), "").trim();
                    String nss = java.util.Objects.toString(p.getNumeroSecuriteSociale(), "").trim();
                    if (q != null && !q.isBlank()) {
                        String ql = q.toLowerCase();
                        if (!(nom.toLowerCase().contains(ql) || prenom.toLowerCase().contains(ql) || nss.toLowerCase().contains(ql))) continue;
                    }
                    var m = new java.util.HashMap<String,Object>();
                    m.put("id", p.getId() == null ? null : p.getId().toString());
                    m.put("nom", nom.isEmpty() ? java.util.Objects.toString(p.getEmail(),"") : nom);
                    m.put("prenom", prenom.isEmpty() ? "" : prenom);
                    m.put("telephone", telephone);
                    m.put("numero_securite_sociale", nss);
                    list.add(m);
                }
            }
            mapper.writeValue(resp.getWriter(), list);
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","invalid_date_or_params","message",ex.getMessage()));
        }
    }
}
