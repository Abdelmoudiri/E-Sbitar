<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Sbitari</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-900 text-white flex items-center justify-center">
<div class="w-full max-w-lg p-8 bg-gray-800 rounded shadow">
    <h1 class="text-2xl mb-6 text-center">Inscription</h1>
    <% if (request.getAttribute("registerError") != null) { %>
    <div class="bg-red-600 text-white p-2 rounded mb-4"><%= request.getAttribute("registerError") %></div>
    <% } %>
    <form action="${pageContext.request.contextPath}/register" method="post" class="space-y-4">
        <div>
            <label class="block text-sm">Nom</label>
            <input type="text" name="nom" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <div>
            <label class="block text-sm">Prénom</label>
            <input type="text" name="prenom" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <div>
            <label class="block text-sm">Email</label>
            <input type="email" name="email" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <div>
            <label class="block text-sm">Mot de passe</label>
            <input type="password" name="password" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <div>
            <label class="block text-sm">Rôle</label>
            <select name="type_person" id="type_person" required class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
                <option value="" disabled selected>Sélectionnez votre rôle</option>
                <option value="infirmier">Infirmier</option>
                <option value="medecin_general">Médecin Généraliste</option>
                <option value="medecin_specialiste">Médecin Spécialiste</option>
            </select>
        </div>

        <div>
            <label class="block text-sm">Téléphone</label>
            <input type="text" name="telephone" class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <div>
            <label class="block text-sm">Service (si infirmier)</label>
            <input type="text" name="service" class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
        </div>

        <!-- Removed patient-specific vitals/measurements: temperature, tension, frequence_cardiaque, frequence_respiratoire, poids, taille -->

        <!-- Spécialité (visible si medecin_specialiste) -->
        <div id="specialite_container" class="hidden">
            <label class="block text-sm">Spécialité</label>
            <input type="text" name="specialite" id="specialite" class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600">
            <label class="block text-sm mt-3">Description de la spécialité (optionnel)</label>
            <textarea name="specialite_description" id="specialite_description" rows="3" class="w-full mt-1 p-2 rounded bg-gray-700 border border-gray-600"></textarea>
        </div>

        <div>
            <button type="submit" class="w-full py-2 px-4 bg-orange-600 rounded">S'inscrire</button>
        </div>

        <div class="text-sm text-center">
            <a href="${pageContext.request.contextPath}/login" class="text-orange-400">Se connecter</a>
        </div>
    </form>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const typePersonSelect = document.getElementById('type_person');
        const specialiteContainer = document.getElementById('specialite_container');
        const specialiteInput = document.getElementById('specialite');
        if (!typePersonSelect) return;

        function updateSpecialiteVisibility() {
            if (typePersonSelect.value === 'medecin_specialiste') {
                specialiteContainer.classList.remove('hidden');
                if (specialiteInput) specialiteInput.required = true;
            } else {
                specialiteContainer.classList.add('hidden');
                if (specialiteInput) specialiteInput.required = false;
            }
        }

        typePersonSelect.addEventListener('change', updateSpecialiteVisibility);
        // initialize visibility on load in case the select has a preselected value
        updateSpecialiteVisibility();
    });
</script>
</body>
</html>