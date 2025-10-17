<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard Spécialiste</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>window.APP_CONTEXT = '${pageContext.request.contextPath}';</script>
</head>
<body class="min-h-screen bg-gray-50 text-gray-800 font-sans">
<div class="max-w-4xl mx-auto p-6">
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    <h1 class="text-2xl font-semibold mb-4">Demandes reçues</h1>
    <div class="mb-4">
        <label class="block text-sm">Filtrer par statut</label>
        <select id="filter-status" class="p-2 border rounded mt-1">
            <option value="">Tous</option>
            <option value="EN_ATTENTE">En attente</option>
            <option value="EN_COURS">En cours</option>
            <option value="TRAITEE">Terminée</option>
        </select>
    </div>
    <div id="demandes-list" class="space-y-3">Chargement...</div>
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
</div>

<script>
    async function loadDemandes() {
        const status = document.getElementById('filter-status').value;
        const url = window.APP_CONTEXT + '/api/specialist/demandes';
        const res = await fetch(url);
        if (!res.ok) { document.getElementById('demandes-list').textContent = 'Erreur chargement'; return; }
        const list = await res.json();
        const filtered = status ? list.filter(d => d.status === status) : list;
        const container = document.getElementById('demandes-list');
        if (!filtered.length) { container.innerHTML = '<div class="text-gray-600">Aucune demande</div>'; return; }
        container.innerHTML = filtered.map(d => {
            return `<div class="bg-white p-4 rounded shadow">
                <div class="flex justify-between"><div><strong>Demande #\${d.id}</strong> — \${d.specialite || ''}</div><div class="text-sm text-gray-600">\${d.createdAt || ''}</div></div>
                <div class="mt-2">Patient: \${escapeHtml(d.patientNom || '')} \${escapeHtml(d.patientPrenom || '')}</div>
                <div class="mt-2">Question: <div class="text-sm text-gray-800">\${escapeHtml(d.question || '')}</div></div>
                <div class="mt-3 flex gap-2">` + (
                    d.status === 'TRAITEE' ? `<div class="text-green-600">Répondu: \${escapeHtml(d.reponse||'')}</div>` : `<button data-id="\${d.id}" class="btn-answer bg-blue-600 text-white px-3 py-1 rounded">Répondre</button>`
                ) + `</div></div>`;
        }).join('');
        // attach handlers
        document.querySelectorAll('.btn-answer').forEach(b => b.addEventListener('click', openAnswerModal));
    }

    function escapeHtml(s) { return String(s||'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }

    function openAnswerModal(ev) {
        const id = ev.currentTarget.getAttribute('data-id');
        let modal = document.getElementById('answer-modal');
        if (modal) modal.remove();
        modal = document.createElement('div');
        modal.id = 'answer-modal';
        modal.innerHTML = `<div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div class="bg-white p-6 rounded w-full max-w-lg">
                <h3 class="text-lg font-medium mb-2">Répondre à la demande #${id}</h3>
                <textarea id="answer-text" rows="6" class="w-full p-2 border rounded" placeholder="Saisir votre avis..."></textarea>
                <div class="flex justify-end gap-2 mt-3"><button id="answer-cancel" class="px-3 py-1 border rounded">Annuler</button><button id="answer-send" class="px-3 py-1 bg-blue-600 text-white rounded">Envoyer</button></div>
            </div></div>`;
        document.body.appendChild(modal);
        document.getElementById('answer-cancel').addEventListener('click', ()=> modal.remove());
        document.getElementById('answer-send').addEventListener('click', async ()=>{
            const reponse = document.getElementById('answer-text').value.trim();
            if (!reponse) { alert('Réponse requise'); return; }
            try {
                const res = await fetch(window.APP_CONTEXT + '/api/specialist/demandes/' + encodeURIComponent(id) + '/answer', {
                    method: 'POST', headers: {'Content-Type':'application/json'},
                    body: JSON.stringify({ reponse: reponse, userId: 1 }) // TODO: replace userId with actual logged-in id
                });
                if (!res.ok) { alert('Erreur serveur'); return; }
                alert('Réponse enregistrée');
                modal.remove();
                loadDemandes();
            } catch (e) { alert('Erreur réseau'); }
        });
    }

    document.getElementById('filter-status').addEventListener('change', loadDemandes);
    </script>
    </body>
    </html>
</html>
