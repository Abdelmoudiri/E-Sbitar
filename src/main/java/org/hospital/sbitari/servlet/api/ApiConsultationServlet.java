package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.Consultation;
import org.hospital.sbitari.entity.Patient;
import org.hospital.sbitari.entity.ConsultationStatus;
import org.hospital.sbitari.service.ConsultationService;
import org.hospital.sbitari.service.PatientService;
import org.hospital.sbitari.service.impl.ConsultationServiceImpl;
import org.hospital.sbitari.service.impl.PatientServiceImpl;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "ApiConsultationServlet", urlPatterns = {"/api/consultation", "/api/consultation/*"})
public class ApiConsultationServlet extends HttpServlet {

    private final ConsultationService consultationService = new ConsultationServiceImpl();
    private final PatientService patientService = new PatientServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");


        var node = mapper.readTree(req.getReader());
        Long patientId = node.path("patientId").asLong(0);
        String motif = node.path("motif").asText(null);

        if (patientId == 0 || motif == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new HashMap<String,String>(){{ put("error","patientId and motif required"); }});
            return;
        }

        Patient patient = patientService.findById(patientId).orElse(null);
        if (patient == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new HashMap<String,String>(){{ put("error","patient not found"); }});
            return;
        }

        Consultation c = new Consultation();
        c.setPatient(patient);
        c.setMotif(motif);
        c.setStatus(ConsultationStatus.OPEN);
        Consultation created = consultationService.create(c);

        mapper.writeValue(resp.getWriter(), java.util.Map.of("id", created.getId()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            var list = consultationService.findAll();
            var outList = list.stream().map(c -> {
                var m = new HashMap();
                m.put("id", c.getId());
                m.put("patientId", c.getPatient() != null ? c.getPatient().getId() : null);
                if (c.getPatient() != null) {
                    m.put("patientNom", c.getPatient().getNom());
                    m.put("patientPrenom", c.getPatient().getPrenom());
                    m.put("patientSSN", c.getPatient().getNumeroSecuriteSociale());
                    m.put("patientTelephone", c.getPatient().getTelephone());
                }
                m.put("motif", c.getMotif());
                m.put("status", c.getStatus() != null ? c.getStatus().name() : null);
                m.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
                if (c.getGeneraliste() != null) {
                    m.put("generalisteId", c.getGeneraliste().getId());
                    m.put("generalisteNom", c.getGeneraliste().getNom());
                    m.put("generalistePrenom", c.getGeneraliste().getPrenom());
                }
                return m;
            }).toList();
            mapper.writeValue(resp.getWriter(), outList);
            return;
        }

        try {
            if (path.length() <= 1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), new HashMap<String,String>(){{ put("error","id required in path"); }});
                return;
            }
            Long id = Long.parseLong(path.substring(1));
            Consultation c = consultationService.findById(id).orElse(null);
            if (c == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","not found"));
                return;
            }

            var out = new HashMap<String,Object>();
            out.put("id", c.getId());
            out.put("patientId", c.getPatient() != null ? c.getPatient().getId() : null);
            if (c.getPatient() != null) {
                out.put("patientNom", c.getPatient().getNom());
                out.put("patientPrenom", c.getPatient().getPrenom());
                out.put("patientSSN", c.getPatient().getNumeroSecuriteSociale());
                out.put("patientTelephone", c.getPatient().getTelephone());
            }
            out.put("motif", c.getMotif());
            out.put("observations", c.getObservations());
            out.put("status", c.getStatus().name());
            out.put("createdAt", c.getCreatedAt().toString());
            if (c.getGeneraliste() != null) {
                out.put("generalisteId", c.getGeneraliste().getId());
                out.put("generalisteNom", c.getGeneraliste().getNom());
                out.put("generalistePrenom", c.getGeneraliste().getPrenom());
            }
            mapper.writeValue(resp.getWriter(), out);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","invalid id"));
        }
    }
}
