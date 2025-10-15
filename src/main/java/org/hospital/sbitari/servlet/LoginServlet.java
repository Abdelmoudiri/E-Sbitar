package org.hospital.sbitari.servlet;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.service.UserService;
import org.hospital.sbitari.service.impl.UserServiceImpl;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email != null) {
            email = email.trim().toLowerCase();
        }
        String password = request.getParameter("password");

        UserService userService = new UserServiceImpl();
        boolean ok = userService.authenticate(email, password);
        if (!ok) {
            request.setAttribute("error", "Identifiants invalides");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userService.findByEmail(email).orElseThrow();
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        switch (user.getRole()) {
            case INFIRMIER:
                response.sendRedirect(request.getContextPath() + "/infirmier/dashboard.jsp");
                break;
            case GENERALIST:
                response.sendRedirect(request.getContextPath() + "/generaliste/dashboard.jsp");
                break;
            case SPECIALIST:
                response.sendRedirect(request.getContextPath() + "/specialiste/dashboard.jsp");
                break;
            default:
                response.sendRedirect(request.getContextPath());
        }
    }

}
