<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="max-w-7xl mx-auto p-6 flex items-center justify-between">
    <div>
        <a href="${pageContext.request.contextPath}/" class="text-xl font-bold text-gray-800">Sbitari</a>
    </div>
    <div class="flex items-center gap-4">
        <span class="text-sm text-gray-600">Connecté en tant que: <strong><%= ((org.hospital.sbitari.entity.User)session.getAttribute("user"))==null?"Invité":((org.hospital.sbitari.entity.User)session.getAttribute("user")).getNom() %></strong></span>
        <a href="${pageContext.request.contextPath}/logout" class="px-3 py-1 bg-red-100 text-red-700 rounded">Déconnexion</a>
    </div>
</header>
