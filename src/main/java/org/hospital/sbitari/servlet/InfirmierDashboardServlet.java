package org.hospital.sbitari.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hospital.sbitari.entity.User;

import java.io.IOException;

@WebServlet("/infirmier/dashboard")
public class InfirmierDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        if (session == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//        Object u = session.getAttribute("user");
//        if (!(u instanceof User)) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//        User user = (User) u;
//        if (user.getRole() == null || !user.getRole().name().equalsIgnoreCase("INFIRMIER")) {
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux infirmiers");
//            return;
//        }
//
//        try {
            req.getRequestDispatcher("/infirmier/dashboard.jsp").forward(req, resp);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.setContentType("application/json;charset=UTF-8");
//            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
//            try {
//                mapper.writeValue(resp.getWriter(), java.util.Map.of("error", "internal", "message", e.toString()));
//            } catch (java.io.IOException ioEx) {
//                resp.getWriter().write("Internal server error");
//            }
       // }
    }
}