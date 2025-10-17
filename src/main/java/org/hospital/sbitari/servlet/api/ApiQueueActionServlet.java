package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.Consultation;
import org.hospital.sbitari.entity.ConsultationStatus;
import org.hospital.sbitari.entity.QueueEntry;
import org.hospital.sbitari.service.ConsultationService;
import org.hospital.sbitari.service.QueueService;
import org.hospital.sbitari.service.impl.ConsultationServiceImpl;
import org.hospital.sbitari.service.impl.QueueServiceImpl;

import java.io.IOException;

@WebServlet(name = "ApiQueueActionServlet", urlPatterns = {"/api/queue/*"})
public class ApiQueueActionServlet extends HttpServlet {

    private final QueueService queueService = new QueueServiceImpl();
    private final ConsultationService consultationService = new ConsultationServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // path: /api/queue/{id}/accept or /api/queue/{id}/reject
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","id/action required"));
            return;
        }
        String[] parts = path.substring(1).split("/");
        if (parts.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","id/action required"));
            return;
        }
        try {
            Long id = Long.parseLong(parts[0]);
            String action = parts[1];
            QueueEntry q = queueService.findById(id).orElse(null);
            if (q == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","queue entry not found"));
                return;
            }

            if ("accept".equalsIgnoreCase(action)) {
                // create consultation for this patient and remove queue entry
                Consultation c = new Consultation();
                c.setPatient(q.getPatient());
                c.setStatus(ConsultationStatus.OPEN);
                Consultation created = consultationService.create(c);
                queueService.deleteById(q.getId());
                mapper.writeValue(resp.getWriter(), java.util.Map.of("consultationId", created.getId()));
                return;
            } else if ("reject".equalsIgnoreCase(action)) {
                queueService.deleteById(q.getId());
                mapper.writeValue(resp.getWriter(), java.util.Map.of("status","deleted"));
                return;
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","unknown action"));
                return;
            }

        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","invalid id"));
        }
    }
}
