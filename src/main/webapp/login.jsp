<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Sbitari</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-900 text-white flex items-center justify-center">
<div class="w-full max-w-md p-8 bg-gray-800 rounded shadow">
    <h1 class="text-2xl mb-6 text-center">Connexion</h1>
    <% if (request.getAttribute("error") != null) { %>
    <div class="bg-red-600 text-white p-2 rounded mb-4"><%= request.getAttribute("error") %></div>
    <% } %>
    <form action="${pageContext.request.contextPath}/login" method="post" class="space-y-4">
        <div>
            <label class="block text-sm">Email</label>
            <input type="email" name="email" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>
        <div>
            <label class="block text-sm">Mot de passe</label>
            <input type="password" name="password" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>
        <div>
            <button type="submit" class="w-full py-2 px-4 bg-orange-600 rounded">Se connecter</button>
        </div>
        <div class="text-sm text-center">
            <a href="${pageContext.request.contextPath}/register" class="text-orange-400">S'inscrire</a>
        </div>
    </form>
</div>
</body>
</html>