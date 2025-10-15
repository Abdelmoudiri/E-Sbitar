package org.hospital.sbitari.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hospital.sbitari.entity.Generalist;
import org.hospital.sbitari.entity.Infirmier;
import org.hospital.sbitari.entity.Specialist;
import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.service.UserService;
import org.hospital.sbitari.service.impl.UserServiceImpl;
import org.hospital.sbitari.dao.SpecialiteDao;
import org.hospital.sbitari.dao.impl.SpecialiteDaoImpl;
import org.hospital.sbitari.entity.Specialite;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        if (email != null) email = email.trim().toLowerCase();
            String rawPassword = request.getParameter("password");
        String typePerson = request.getParameter("type_person");
        String specialite = request.getParameter("specialite");
        String specialiteDescription = request.getParameter("specialite_description");
        String telephone = request.getParameter("telephone");
        String service = request.getParameter("service");
        String tarifStr = request.getParameter("tarif");

        if (typePerson != null && typePerson.equalsIgnoreCase("patient")) {
            request.setAttribute("registerError", "L'inscription des patients est désactivée. Contactez un infirmier pour créer un compte patient.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        UserService userService = new UserServiceImpl();

        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank() || email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            request.setAttribute("registerError", "Nom, prénom, email et mot de passe sont obligatoires.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (userService.findByEmail(email).isPresent()) {
            request.setAttribute("registerError", "Un compte avec cet email existe déjà.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User user = null;
        if ("infirmier".equals(typePerson)) {
            user = new Infirmier(nom, prenom, email, rawPassword, null, null);
        } else if ("medecin_general".equals(typePerson)) {
            user = new Generalist(nom, prenom, email, rawPassword);
        } else if ("medecin_specialiste".equals(typePerson)) {
            SpecialiteDao specialiteDao = new SpecialiteDaoImpl();
            Specialite spEntity = null;
            if (specialite != null && !specialite.isBlank()) {
                spEntity = specialiteDao.findByName(specialite).orElse(null);
                if (spEntity == null) {
                    spEntity = new Specialite();
                    spEntity.setNom(specialite);
                    if (specialiteDescription != null && !specialiteDescription.isBlank()) {
                        spEntity.setDescription(specialiteDescription);
                    }
                    spEntity = specialiteDao.save(spEntity);
                }
            }

            user = new Specialist(nom, prenom, email, rawPassword, spEntity, null);
        }

        if (user == null) {
            request.setAttribute("registerError", "Type d'utilisateur invalide");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // populate fields according to concrete type
        if (user instanceof Infirmier) {
            Infirmier inf = (Infirmier) user;
            inf.setTelephone(telephone);
            inf.setService(service);


        } else if (user instanceof Specialist) {
            Specialist sp = (Specialist) user;
            // specialite entity was already set when constructing the Specialist
            if (tarifStr != null && !tarifStr.isEmpty()) {
                try { sp.setTarif(Double.parseDouble(tarifStr)); } catch (NumberFormatException ignored) {}
            }
        } else if (user instanceof Generalist) {
            Generalist g = (Generalist) user;
            if (tarifStr != null && !tarifStr.isEmpty()) {
                try { g.setTarif(Double.parseDouble(tarifStr)); } catch (NumberFormatException ignored) {}
            }
        }


        userService.create(user, rawPassword);

        response.sendRedirect(request.getContextPath() + "/infirmier/dashboard");
    }
}
