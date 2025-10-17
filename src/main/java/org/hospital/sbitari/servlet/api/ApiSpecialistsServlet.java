package org.hospital.sbitari.servlet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ApiSpecialistsServlet", urlPatterns = {"/api/specialists"})
public class ApiSpecialistsServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        // return a mocked list for now; later wire to DAO
        var list = java.util.List.of(
                java.util.Map.of("id",1,"nom","Ahmed","prenom","Boukili","specialite","Dermatologie"),
                java.util.Map.of("id",2,"nom","Leila","prenom","Ben","specialite","Cardiologie")
        );
        mapper.writeValue(resp.getWriter(), list);
    }
}
