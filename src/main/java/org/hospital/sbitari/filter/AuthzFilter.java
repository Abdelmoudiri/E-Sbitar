package org.hospital.sbitari.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hospital.sbitari.entity.User;
import org.hospital.sbitari.entity.enums.Role;

import java.io.IOException;
import java.util.*;


@WebFilter("/*")
public class AuthzFilter implements Filter {

    // map prefix -> allowed roles
    private final Map<String, Set<Role>> protectedPaths = new LinkedHashMap<>();

    // allowlist for public resources
    private final Set<String> allowlist = new HashSet<>(Arrays.asList(
            "/login", "/register", "/index.jsp", "/", "/css/", "/js/", "/images/", "/api/public"
    ));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // configure required roles for URL prefixes
        protectedPaths.put("/infirmier", EnumSet.of(Role.INFIRMIER));
        protectedPaths.put("/api/infirmier", EnumSet.of(Role.INFIRMIER));
    // généraliste routes
    protectedPaths.put("/generaliste", EnumSet.of(Role.GENERALIST));
    protectedPaths.put("/api/generaliste", EnumSet.of(Role.GENERALIST));
        // admin-style endpoints could be added later
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        // trivial static resource bypass
        for (String p : allowlist) {
            if (path.equals(p) || path.startsWith(p)) {
                // if user is authenticated, prevent accessing login/register
                if ((p.equals("/login") || p.equals("/register"))) {
                    HttpSession s = request.getSession(false);
                    if (s != null && s.getAttribute("user") instanceof User) {
                        // redirect to already-logged-in page
                        response.sendRedirect(request.getContextPath() + "/already-logged-in.jsp");
                        return;
                    }
                }
                chain.doFilter(req, res);
                return;
            }
        }

        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            Object u = session.getAttribute("user");
            if (u instanceof User) user = (User) u;
        }

        // find the first protected prefix that matches
        for (Map.Entry<String, Set<Role>> e : protectedPaths.entrySet()) {
            String prefix = e.getKey();
            if (path.startsWith(prefix)) {
                // need auth
                if (user == null) {
                    handleUnauthenticated(request, response);
                    return;
                }
                // check roles
                Role r = user.getRole();
                if (r == null || !e.getValue().contains(r)) {
                    handleForbidden(request, response);
                    return;
                }
                break; // matched and allowed
            }
        }

        // default: allow
        chain.doFilter(req, res);
    }

    private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isApi(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"unauthenticated\"}");
        } else {
            // redirect to login with returnTo
            String returnTo = request.getRequestURI().substring(request.getContextPath().length());
            response.sendRedirect(request.getContextPath() + "/login?returnTo=" + java.net.URLEncoder.encode(returnTo, "UTF-8"));
        }
    }

    private void handleForbidden(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isApi(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"forbidden\"}");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
        }
    }

    private boolean isApi(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String accept = request.getHeader("Accept");
        return path.startsWith("/api/") || (accept != null && accept.contains("application/json"));
    }

    @Override
    public void destroy() {

    }
}
