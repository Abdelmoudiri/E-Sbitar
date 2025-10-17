package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.QueueEntry;
import org.hospital.sbitari.service.QueueService;
import org.hospital.sbitari.service.impl.QueueServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ApiGeneralisteQueueServlet", urlPatterns = {"/api/generaliste/queue"})
public class ApiGeneralisteQueueServlet extends HttpServlet {

    private final QueueService queueService = new QueueServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        LocalDate today = LocalDate.now();
        List<QueueEntry> list = queueService.findByDate(today);
        var out = list.stream().map(q -> {
            var m = new java.util.HashMap<String,Object>();
            m.put("id", q.getId());
            if (q.getPatient() != null) {
                m.put("patientId", q.getPatient().getId().toString());
                m.put("nom", q.getPatient().getNom());
                m.put("prenom", q.getPatient().getPrenom());
                m.put("telephone", q.getPatient().getTelephone());
                m.put("numero_securite_sociale", q.getPatient().getNumeroSecuriteSociale());
            }
            m.put("createdAt", q.getCreatedAt().toString());
            return m;
        }).collect(Collectors.toList());
        mapper.writeValue(resp.getWriter(), out);
    }
}
