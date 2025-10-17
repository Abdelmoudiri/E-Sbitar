package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.DemandeExpertise;
import org.hospital.sbitari.entity.DemandeExpertiseStatus;
import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.service.DemandeExpertiseService;
import org.hospital.sbitari.service.UserService;
import org.hospital.sbitari.service.impl.DemandeExpertiseServiceImpl;
import org.hospital.sbitari.service.impl.UserServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ApiSpecialistDemandesServlet", urlPatterns = {"/api/specialist/demandes","/api/specialist/demandes/*"})
public class ApiSpecialistDemandesServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DemandeExpertiseService demandeService = new DemandeExpertiseServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String specialistIdParam = req.getParameter("specialistId");
            java.util.List<java.util.Map<String,Object>> out = demandeService.findAllAsMap();
            if (specialistIdParam != null) {
                try {
                    Long sid = Long.parseLong(specialistIdParam);
                    out = out.stream().filter(m -> m.get("patientId") != null && m.get("patientId") instanceof Long ? true : true).collect(Collectors.toList());
                    // Note: filtering by specialist id requires specialist information in the map; currently we return all demandes.
                } catch (NumberFormatException ex) {
                    // ignore
                }
            }
            mapper.writeValue(resp.getWriter(), out);
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error", ex.getMessage()==null?"server error":ex.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo(); // expects /{id}/answer
        if (path == null || path.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","id required"));
            return;
        }
        String[] parts = path.substring(1).split("/");
        try {
            Long id = Long.parseLong(parts[0]);
            var node = mapper.readTree(req.getReader());
            String reponse = node.path("reponse").asText(null);
            Long userId = node.path("userId").asLong(0);

            DemandeExpertise d = demandeService.findById(id).orElse(null);
            if (d == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","demande not found"));
                return;
            }
            if (reponse == null || reponse.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), java.util.Map.of("error","reponse required"));
                return;
            }

            d.setReponse(reponse);
            d.setStatus(DemandeExpertiseStatus.TRAITEE);
            d.setHandledAt(LocalDateTime.now());
            if (userId != null && userId > 0) {
                User u = userService.findById(userId).orElse(null);
                if (u != null) d.setHandledBy(u);
            }
            demandeService.create(d); // save/update
            mapper.writeValue(resp.getWriter(), java.util.Map.of("status","ok"));
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Map.of("error","invalid id"));
        }
    }
}
