<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.hospital.sbitari.entity.User" %>
<%@ page import="org.hospital.sbitari.entity.enums.Role" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Déjà connecté</title>
    <link rel="stylesheet" href="https://cdn.tailwindcss.com">
</head>
<body class="min-h-screen bg-gray-50 text-gray-800 font-sans p-6">
<div class="max-w-2xl mx-auto bg-white rounded p-6 shadow">
    <h1 class="text-xl font-semibold mb-4">Vous êtes déjà connecté</h1>
    <%
        User user = null;
        if (request.getSession(false) != null) {
            Object u = request.getSession(false).getAttribute("user");
            if (u instanceof User) user = (User) u;
        }
    %>
    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            <p>Bonjour, <strong>${sessionScope.user.nom} ${sessionScope.user.prenom}</strong> (<em>${sessionScope.user.role}</em>).</p>
        </c:when>
        <c:otherwise>
            <p>Vous êtes déjà connecté.</p>
        </c:otherwise>
    </c:choose>

    <div class="mt-4 space-y-2">
        <p>Aller vers votre tableau de bord :</p>
        <ul class="list-disc pl-6">
            <li><a class="text-blue-600" href="${pageContext.request.contextPath}/infirmier/dashboard.jsp">Tableau Infirmier</a></li>
            <li><a class="text-blue-600" href="${pageContext.request.contextPath}/generaliste/dashboard.jsp">Tableau Généraliste</a></li>
            <li><a class="text-blue-600" href="${pageContext.request.contextPath}/specialist/dashboard.jsp">Tableau Spécialiste</a></li>
            <li><a class="text-blue-600" href="${pageContext.request.contextPath}/">Accueil</a></li>
        </ul>
    </div>

    <div class="mt-6">
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit" class="px-4 py-2 bg-red-600 text-white rounded">Se déconnecter</button>
        </form>
    </div>
</div>
</body>
</html>
