<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard Généraliste</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 text-gray-800 font-sans">
<script>window.APP_CONTEXT = '${pageContext.request.contextPath}';</script>
<div class="max-w-7xl mx-auto p-6">
    <jsp:include page="/WEB-INF/includes/header.jsp" />

    <h1 class="text-2xl font-semibold mb-4">Tableau de bord - Généraliste</h1>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div class="bg-white p-4 rounded shadow">
            <h2 class="font-medium">Patients récents</h2>
            <div id="gen-patients" class="mt-3 text-sm text-gray-700">Chargement...</div>
        </div>

        <div class="bg-white p-4 rounded shadow">
            <h2 class="font-medium">File d'attente</h2>
            <div id="list-attend" class="mt-3 text-sm text-gray-700">Chargement...</div>
        </div>

        <div class="bg-white p-4 rounded shadow">
            <h2 class="font-medium">Mes consultations</h2>
            <div id="gen-consults" class="mt-3 text-sm text-gray-700">Aucune consultation pour l'instant</div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/includes/footer.jsp" />
</div>

<script>
    async function loadGenPatients() {
        try {
            const url = window.APP_CONTEXT + '/api/patients';
            const res = await fetch(url);
            if (!res.ok) {
                let text = '';
                try { text = await res.text(); } catch (e) { text = '<no body>'; }
                console.debug('generaliste: fetch failed', res.status, text);
                document.getElementById('gen-patients').innerHTML = 'Erreur chargement (status=' + res.status + ')<pre class="mt-2 text-xs text-red-600">' + escapeHtml(text) + '</pre>';
                return;
            }
            const data = await res.json();
            document.getElementById('gen-patients').innerHTML = data.slice(0,10).map(function(p){
                return '<div>' + escapeHtml(p.nom||'') + ' ' + escapeHtml(p.prenom||'') + ' <span class="text-xs text-gray-500">(' + escapeHtml(p.numero_securite_sociale||'') + ')</span></div>';
            }).join('');
        } catch (e) {
            document.getElementById('gen-patients').textContent = 'Erreur réseau: ' + (e && e.message ? e.message : 'unknown');
        }
    }

    async function loadConsultations()
    {
        const resDiv=document.getElementById("gen-consults");
        try {
            const url = window.APP_CONTEXT + '/api/consultation';
            const res = await fetch(url);
            if (!res.ok) {
                let text = '';
                try {
                    text = await res.text();
                } catch (e) {
                    text = '<no body>';
                }
                resDiv.innerHTML = 'Erreur chargement (status=' + res.status + ')<pre class="mt-2 text-xs text-red-600">' + escapeHtml(text) + '</pre>';
                return;
            }
            const data = await res.json();
            resDiv.innerHTML = data.map(function (p) {
                const created = p.createdAt ? new Date(p.createdAt).toLocaleString() : '';
                return '<div class="py-2 border-b last:border-b-0 flex justify-between items-start">'
                    + '<div>'
                    + '<div class="font-medium">' + escapeHtml(p.patientNom || 'Sans motif') + '</div>'
                    + '<div class="font-medium">' + escapeHtml(p.patientPrenom || 'Sans motif') + '</div>'
                    + '<div class="text-xs text-gray-500">' + escapeHtml(p.status || '') + ' • ' + escapeHtml(created) + '</div>'
                    + '</div>'
                    + '<div class="ml-4">'
                    + '<button class="open-consult-btn bg-blue-500 hover:bg-blue-600 text-white text-xs px-3 py-1 rounded" data-consultation-id="' + escapeHtml(p.id || '') + '">Ouvrir</button>'
                    + '</div>'
                    + '</div>';
            }).join('');

            resDiv.addEventListener('click', function(ev){
                const btn = ev.target.closest && ev.target.closest('.open-consult-btn');
                if (!btn) return;
                const id = btn.dataset && btn.dataset.consultationId;
                if (!id) return;
                window.location.href = window.APP_CONTEXT + '/generaliste/consultation.jsp?id=' + encodeURIComponent(id);
            });
        }catch (e) {
            resDiv.textContent = 'Erreur réseau: ' + (e && e.message ? e.message : 'unknown');
        }

    }

    function escapeHtml(s) {
        return String(s)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
    loadGenPatients();
    loadQueue();
    loadConsultations();

    async function loadQueue() {
        const el = document.getElementById('list-attend');
        try {
            const url = window.APP_CONTEXT + '/api/generaliste/queue';
            const res = await fetch(url);
            if (!res.ok) {
                const txt = await res.text().catch(()=>'<no body>');
                el.innerHTML = 'Erreur chargement file (status=' + res.status + ')<pre class="mt-2 text-xs text-red-600">' + escapeHtml(txt) + '</pre>';
                return;
            }
            const data = await res.json();
            if (!Array.isArray(data) || data.length === 0) {
                el.textContent = "Aucune personne en attente pour aujourd'hui";
                return;
            }
            el.innerHTML = data.map(function(item, idx){
                // support two possible API shapes:
                // 1) item.patient = { id, nom, prenom, ... }
                // 2) flattened: item.patientId, item.nom, item.prenom, ...
                const p = item.patient || {
                    id: item.patientId || item.patientId,
                    nom: item.nom || '',
                    prenom: item.prenom || '',
                    numero_securite_sociale: item.numero_securite_sociale || '',
                    telephone: item.telephone || ''
                };
                const time = item.createdAt ? new Date(item.createdAt).toLocaleTimeString() : '';
                const queueId = item.id || item.queueId || '';
                return '<div class="py-2 border-b last:border-b-0 flex justify-between items-start" data-queue-id="' + escapeHtml(queueId) + '">'
                    + '<div>'
                    + '<div class="font-medium">' + escapeHtml(p.nom||'') + ' ' + escapeHtml(p.prenom||'') + '</div>'
                    + '<div class="text-xs text-gray-500">SSN: ' + escapeHtml(p.numero_securite_sociale||'') + ' • ' + escapeHtml(time) + '</div>'
                    + '</div>'
                    + '<div class="flex gap-2 ml-4">'
                    + '<button class="accept-btn bg-green-500 hover:bg-green-600 text-white text-xs px-3 py-1 rounded" data-queue-id="' + escapeHtml(queueId) + '" data-patient-id="' + escapeHtml(p.id || '') + '">Accepter</button>'
                    + '<button class="reject-btn bg-red-500 hover:bg-red-600 text-white text-xs px-3 py-1 rounded" data-queue-id="' + escapeHtml(queueId) + '" data-patient-id="' + escapeHtml(p.id || '') + '">Refuser</button>'
                    + '</div>'
                    + '</div>';
            }).join('');

            // event delegation for accept/refuse buttons
            el.querySelectorAll('.accept-btn, .reject-btn');
            el.addEventListener('click', async function(ev){
                const btn = ev.target.closest('button');
                if (!btn) return;
                if (!btn.dataset || !btn.dataset.queueId) return;
                const qid = btn.dataset.queueId;
                const action = btn.classList.contains('accept-btn') ? 'accept' : (btn.classList.contains('reject-btn') ? 'reject' : null);
                if (!action) return;
                if (action === 'reject') {
                    if (!confirm('Confirmer le refus de cette personne ?')) return;
                }
                const url = window.APP_CONTEXT + '/api/queue/' + encodeURIComponent(qid) + '/' + action;
                try {
                    const r = await fetch(url, { method: 'POST' });
                    if (!r.ok) {
                        const txt = await r.text().catch(()=>'');
                        alert('Erreur serveur: ' + r.status + '\n' + txt);
                        return;
                    }
                    // on success remove the entry from the UI
                    const row = btn.closest('[data-queue-id]');
                    if (row) row.remove();
                } catch (err) {
                    alert('Erreur réseau: ' + (err && err.message ? err.message : 'unknown'));
                }
            });
        } catch (e) {
            el.textContent = 'Erreur réseau: ' + (e && e.message ? e.message : 'unknown');
        }
    }
</script>
</body>
</html>