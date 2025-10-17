package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "ApiSpecialistSlotsServlet", urlPatterns = {"/api/specialist/*/slots"})
public class ApiSpecialistSlotsServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        // very small mock: produce 4 slots for today
        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime now = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        var list = new java.util.ArrayList<java.util.Map<String,Object>>();
        for (int i=0;i<4;i++) {
            java.util.Map<String,Object> s = new java.util.HashMap<>();
            s.put("start", now.plusHours(i).format(f));
            s.put("end", now.plusHours(i+1).format(f));
            s.put("available", true);
            list.add(s);
        }
        mapper.writeValue(resp.getWriter(), list);
    }
}
