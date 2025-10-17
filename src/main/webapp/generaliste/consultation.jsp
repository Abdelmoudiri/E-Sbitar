<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Consultation</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>window.APP_CONTEXT = '${pageContext.request.contextPath}';</script>
</head>
<body class="min-h-screen bg-gray-50 text-gray-800 font-sans">
<div class="max-w-3xl mx-auto p-6">
    <jsp:include page="/WEB-INF/includes/header.jsp" />

    <h1 class="text-2xl font-semibold mb-4">Consultation</h1>

    <div id="consult-card" class="bg-white p-4 rounded shadow">Chargement...</div>

    <jsp:include page="/WEB-INF/includes/footer.jsp" />
</div>

<script>
    async function loadConsultation() {
        const params = new URLSearchParams(window.location.search);
        const id = params.get('id');
        const el = document.getElementById('consult-card');
        if (!id) { el.textContent = 'ID consultation manquant'; return; }
        try {
            const url = window.APP_CONTEXT + '/api/consultation/' + encodeURIComponent(id);
            const res = await fetch(url);
            if (!res.ok) {
                el.innerHTML = 'Erreur chargement (status=' + res.status + ') ' + await res.text();
                return;
            }
            const c = await res.json();
            el.innerHTML = '<div class="font-medium text-lg">' + escapeHtml(c.motif || 'Sans motif') + '</div>'
                + '<div class="text-sm text-gray-600">Statut: ' + escapeHtml(c.status || '') + ' • ' + escapeHtml(c.createdAt || '') + '</div>'
                + '<hr class="my-3" />'
                + '<div><strong>Patient:</strong> ' + escapeHtml(c.patientNom || '') + ' ' + escapeHtml(c.patientPrenom || '') + ' (' + escapeHtml(c.patientSSN || '') + ')</div>'
                + '<div><strong>Téléphone:</strong> ' + escapeHtml(c.patientTelephone || '') + '</div>'
                + '<hr class="my-3" />'
                    + '<div><strong>Observations</strong><div class="mt-2">' + escapeHtml(c.observations || 'Aucune') + '</div></div>'
                    + '<div class="mt-4">'
                    + '<button id="choose-specialist-btn" class="bg-blue-600 text-white px-4 py-2 rounded-lg shadow hover:bg-blue-700 transition duration-300">Choisir un spécialiste</button>'
                    + '</div>';
        } catch (e) {
            el.textContent = 'Erreur réseau: ' + (e && e.message ? e.message : 'unknown');
        }
    }

    function escapeHtml(s) {
        return String(s)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
    loadConsultation();

    // Modal for choosing a specialist
    document.addEventListener('click', function(ev){
        const btn = ev.target.closest && ev.target.closest('#choose-specialist-btn');
        if (!btn) return;
        openChooseSpecialistModal();
    });

    async function openChooseSpecialistModal() {
        // create modal if not existing
        let modal = document.getElementById('choose-specialist-modal');
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'choose-specialist-modal';
            modal.innerHTML = '\n<div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">'
                + '<div class="bg-white rounded p-6 w-full max-w-md">'
                + '<h3 class="text-lg font-medium mb-2">Demander un avis spécialiste</h3>'
                + '<div class="mb-3">'
                + '<label class="block text-sm">Spécialité</label>'
                + '<select id="modal-specialite" class="w-full mt-1 p-2 border rounded"></select>'
                + '</div>'
                    + '<div class="mb-3">'
                    + '<label class="block text-sm">Priorité</label>'
                    + '<select id="modal-priority" class="w-full mt-1 p-2 border rounded">'
                    + '<option value="NORMALE">Normale</option>'
                    + '<option value="URGENTE">Urgente</option>'
                    + '<option value="NON_URGENTE">Non urgente</option>'
                    + '</select>'
                    + '</div>'
                    + '<div class="mb-3">'
                    + '<label class="block text-sm">Question / contexte</label>'
                    + '<textarea id="modal-question" class="w-full mt-1 p-2 border rounded" rows="4"></textarea>'
                    + '</div>'
                    + '<div class="mb-3">'
                    + '<label class="block text-sm">Pièces / liens (un par ligne)</label>'
                    + '<textarea id="modal-attachments" class="w-full mt-1 p-2 border rounded" rows="3" placeholder="https://..."></textarea>'
                    + '</div>'
                + '<div class="mb-3">'
                + '<label class="block text-sm">Spécialiste</label>'
                + '<select id="modal-specialist" class="w-full mt-1 p-2 border rounded"></select>'
                + '</div>'
                + '<div class="flex justify-end gap-2">'
                + '<button id="modal-cancel" class="px-3 py-1 rounded border">Annuler</button>'
                + '<button id="modal-send" class="px-3 py-1 rounded bg-blue-600 text-white">Envoyer la demande</button>'
                + '</div>'
                + '</div>'
                + '</div>';
            document.body.appendChild(modal);

            document.getElementById('modal-cancel').addEventListener('click', function(){ modal.remove(); });
            document.getElementById('modal-specialite').addEventListener('change', function(){ populateSpecialists(this.value); });
                document.getElementById('modal-send').addEventListener('click', async function(){
                const specialistId = document.getElementById('modal-specialist').value;
                const specialite = document.getElementById('modal-specialite').value;
                const priority = document.getElementById('modal-priority').value;
                const question = document.getElementById('modal-question').value.trim();
                const attachmentsRaw = document.getElementById('modal-attachments').value.trim();
                const attachments = attachmentsRaw ? attachmentsRaw.split(/\r?\n/).map(s=>s.trim()).filter(Boolean) : [];
                if (!specialite) { alert('Choisissez une spécialité'); return; }
                if (!specialistId) { alert('Choisissez un spécialiste'); return; }
                // send request to server
                const params = new URLSearchParams(window.location.search);
                const id = params.get('id');
                try {
                    const res = await fetch(window.APP_CONTEXT + '/api/consultation-request/' + encodeURIComponent(id), {
                        method: 'POST', headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ specialistId: specialistId, specialite: specialite, priority: priority, question: question, attachments: attachments })
                    });
                    if (!res.ok) {
                        const txt = await res.text().catch(()=>'');
                        alert('Erreur serveur: ' + res.status + '\n' + txt);
                        return;
                    }
                    alert('Demande envoyée');
                    modal.remove();
                } catch (e) {
                    alert('Erreur réseau: ' + (e && e.message ? e.message : 'unknown'));
                }
            });
        }

        // load specialists and specialties
        try {
            const res = await fetch(window.APP_CONTEXT + '/api/specialists');
            if (!res.ok) { alert('Impossible de charger les spécialistes'); return; }
            const list = await res.json();
            const specSet = [];
            const bySpec = {};
            list.forEach(sp => {
                const sname = sp.specialite || 'Autre';
                if (!specSet.includes(sname)) specSet.push(sname);
                if (!bySpec[sname]) bySpec[sname] = [];
                bySpec[sname].push(sp);
            });
            const selSpec = document.getElementById('modal-specialite');
            const selSpecList = document.getElementById('modal-specialist');
            selSpec.innerHTML = '<option value="">-- Choisir spécialité --</option>' + specSet.map(s => '<option value="' + escapeHtml(s) + '">' + escapeHtml(s) + '</option>').join('');
            selSpecList.innerHTML = '<option value="">-- Choisissez une spécialité d\'abord --</option>';
            // attach helper to populateSpecialists
            window.__bySpec = bySpec;
        } catch (e) { alert('Erreur réseau'); }

        function populateSpecialists(specialite) {
            const sel = document.getElementById('modal-specialist');
            const arr = window.__bySpec && window.__bySpec[specialite] ? window.__bySpec[specialite] : [];
            sel.innerHTML = arr.length ? arr.map(sp => '<option value="' + sp.id + '">' + (sp.nom + ' ' + (sp.prenom||'')) + '</option>').join('') : '<option value="">Aucun spécialiste</option>';
        }
    }
</script>
</body>
</html>
