package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hospital.sbitari.entity.DemandeExpertise;
import org.hospital.sbitari.entity.Consultation;
import org.hospital.sbitari.entity.Specialist;
import org.hospital.sbitari.service.DemandeExpertiseService;
import org.hospital.sbitari.service.ConsultationService;
import org.hospital.sbitari.service.impl.DemandeExpertiseServiceImpl;
import org.hospital.sbitari.service.impl.ConsultationServiceImpl;
import org.hospital.sbitari.dao.impl.SpecialistDaoImpl;

@WebServlet(name = "ApiConsultationRequestExpertServlet", urlPatterns = {"/api/consultation-request/*"})
public class ApiConsultationRequestExpertServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo(); // expects /{id}
        if (path == null || path.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","id required"));
            return;
        }
        String[] parts = path.substring(1).split("/");
        try {
            Long id = Long.parseLong(parts[0]);
            var node = mapper.readTree(req.getReader());
            Long specialistId = node.path("specialistId").asLong(0);
            String specialite = node.path("specialite").asText(null);
            String question = node.path("question").asText(null);
            String priorityStr = node.path("priority").asText(null);
            var attachmentsNode = node.path("attachments");

            ConsultationService consultationService = new ConsultationServiceImpl();
            DemandeExpertiseService demandeService = new DemandeExpertiseServiceImpl();

            Consultation consultation = consultationService.findById(id).orElse(null);
            if (consultation == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","consultation not found"));
                return;
            }

            DemandeExpertise d = new DemandeExpertise();
            d.setConsultation(consultation);
            d.setSpecialite(specialite);
            d.setQuestion(question);
            if (priorityStr != null && !priorityStr.isBlank()) {
                try {
                    d.setPriority(org.hospital.sbitari.entity.DemandePriority.valueOf(priorityStr));
                } catch (IllegalArgumentException ex) {
                    // ignore invalid priority - keep default
                }
            }
            if (attachmentsNode != null && attachmentsNode.isArray()) {
                java.util.List<String> attachments = new java.util.ArrayList<>();
                for (var it = attachmentsNode.elements(); it.hasNext(); ) {
                    var n = it.next();
                    if (n.isTextual()) attachments.add(n.asText());
                }
                d.setAttachments(attachments);
            }
            if (specialistId != null && specialistId > 0) {
                // try to load the Specialist by id
                try {
                    var sdao = new SpecialistDaoImpl();
                    sdao.findById(specialistId).ifPresent(d::setSpecialist);
                } catch (Exception ex) {
                    // ignore, fallback to not setting specialist
                }
            }

            DemandeExpertise created = demandeService.create(d);

                System.out.println("DemandeExpertise created: id=" + (created != null ? created.getId() : "null"));
            mapper.writeValue(resp.getWriter(), java.util.Map.of("status","ok","demandeId", created != null ? created.getId() : null));
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","invalid id"));
        } catch (Exception ex) {
            // log stacktrace and return a JSON 500 with message
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = ex.getMessage() == null ? "server error" : ex.getMessage();
            try {
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", msg));
            } catch (IOException ioe) {
                // nothing much we can do here
                ioe.printStackTrace();
            }
        }
    }
}
