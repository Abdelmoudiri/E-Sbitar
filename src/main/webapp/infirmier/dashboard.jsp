<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard Infirmier</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <style>
        /* small custom styles for nicer card shadows */
        .card { box-shadow: 0 6px 18px rgba(15,23,42,0.08); }
    </style>
</head>
<body class="min-h-screen bg-gray-50 text-gray-800 font-sans">
<script>window.APP_CONTEXT = '${pageContext.request.contextPath}';</script>
<div class="max-w-7xl mx-auto p-6">
    <jsp:include page="/WEB-INF/includes/header.jsp" />

    <nav class="mb-6">
        <ul class="flex gap-3">
            <li><button id="tab-patients" class="tab-btn px-4 py-2 rounded bg-white card">Gestion des patients</button></li>
            <li><button id="tab-queue" class="tab-btn px-4 py-2 rounded bg-white card">File d'attente</button></li>
            <li><button id="tab-stats" class="tab-btn px-4 py-2 rounded bg-white card">Statistiques</button></li>
        </ul>
    </nav>

    <main>
        <section id="section-patients" class="space-y-4">
            <div class="flex items-center justify-between">
                <h2 class="text-lg font-semibold">Patients</h2>
                <button id="btn-new-patient" class="px-3 py-1 bg-blue-600 text-white rounded">Nouveau patient</button>
            </div>

            <div class="bg-white p-4 rounded card">
                <table class="w-full table-auto text-sm">
                    <thead>
                    <tr class="text-left text-gray-600">
                        <th class="p-2">N°</th>
                        <th class="p-2">Nom</th>
                        <th class="p-2">Prénom</th>
                        <th class="p-2">Téléphone</th>
                        <th class="p-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody id="patients-tbody">
                    <!-- placeholder rows populated by JS -->
                    </tbody>
                </table>
            </div>
        </section>

        <section id="section-queue" class="hidden space-y-4">
            <div  class="flex items-center justify-between">
                <h2 class="text-lg font-semibold">File d'attente</h2>
                <div id="list-attend" class="text-sm text-gray-600">Actualisé: <span id="queue-timestamp">—</span></div>
            </div>
            <div class="bg-white p-4 rounded card">
                <ul id="queue-list" class="space-y-2 text-sm">
                    <!-- placeholder items -->
                </ul>
            </div>
        </section>

        <section id="section-stats" class="hidden space-y-4">
            <h2 class="text-lg font-semibold">Statistiques</h2>
            <div class="grid grid-cols-2 gap-4">
                <div class="bg-white p-4 rounded card">
                    <canvas id="chart-patients" height="180"></canvas>
                </div>
                <div class="bg-white p-4 rounded card">
                    <h3 class="font-medium mb-2">Indicateurs</h3>
                    <ul class="text-sm space-y-2">
                        <li>Total patients: <strong id="stat-total">0</strong></li>
                        <li>En attente: <strong id="stat-waiting">0</strong></li>
                        <li>Consultations aujourd'hui: <strong id="stat-today">0</strong></li>
                    </ul>
                </div>
            </div>
        </section>
    </main>
    <script>
        function createModal() {
            const overlay = document.createElement('div');
            overlay.className = 'fixed inset-0 bg-black/40 flex items-center justify-center z-50';

            const box = document.createElement('div');
            box.className = 'w-full max-w-xl bg-white rounded p-6';
            box.innerHTML = `
            <h3 class="text-lg font-semibold mb-2">Créer un nouveau patient</h3>
            <div class="space-y-3">
                <div>
                    <label class="block text-sm">Numéro sécurité sociale</label>
                    <input id="modal-ssn" class="w-full p-2 border rounded" />
                </div>
                    <div class="flex gap-2">
                    <button type="button" id="btn-verify-ssn" class="px-3 py-1 bg-blue-600 text-white rounded">Vérifier</button>
                    <button type="button" id="btn-clear-ssn" class="px-3 py-1 bg-gray-200 rounded">Effacer</button>
                </div>
                <div id="modal-alert" class="text-sm text-red-600"></div>

                <div class="grid grid-cols-2 gap-3">
                    <div>
                        <label class="block text-sm">Nom</label>
                        <input id="modal-nom" class="w-full p-2 border rounded" />
                    </div>
                    <div>
                        <label class="block text-sm">Prénom</label>
                        <input id="modal-prenom" class="w-full p-2 border rounded" />
                    </div>
                </div>
                <div class="grid grid-cols-2 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Température (°C)</label>
                        <input id="modal-temperature" type="number" step="0.1" class="w-full p-2 border rounded" />
                    </div>
                    <div>
                        <label class="block text-sm">Tension (ex: 120/80)</label>
                        <input id="modal-tension" class="w-full p-2 border rounded" />
                    </div>
                </div>
                <div class="grid grid-cols-1 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Antécédents médicaux</label>
                        <textarea id="modal-antecedents" class="w-full p-2 border rounded" rows="3"></textarea>
                    </div>
                </div>
                <div class="grid grid-cols-1 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Allergies</label>
                        <textarea id="modal-allergies" class="w-full p-2 border rounded" rows="2"></textarea>
                    </div>
                </div>
                <div class="grid grid-cols-1 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Traitements en cours</label>
                        <textarea id="modal-traitements" class="w-full p-2 border rounded" rows="2"></textarea>
                    </div>
                </div>
                <div class="grid grid-cols-2 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Fréq. cardiaque (bpm)</label>
                        <input id="modal-fc" type="number" class="w-full p-2 border rounded" />
                    </div>
                    <div>
                        <label class="block text-sm">Fréq. respiratoire</label>
                        <input id="modal-fr" type="number" class="w-full p-2 border rounded" />
                    </div>
                </div>
                <div class="grid grid-cols-2 gap-3 mt-2">
                    <div>
                        <label class="block text-sm">Poids (kg)</label>
                        <input id="modal-poids" type="number" step="0.1" class="w-full p-2 border rounded" />
                    </div>
                    <div>
                        <label class="block text-sm">Taille (m)</label>
                        <input id="modal-taille" type="number" step="0.01" class="w-full p-2 border rounded" />
                    </div>
                </div>
                <div>
                    <label class="block text-sm">Email</label>
                    <input id="modal-email" type="email" class="w-full p-2 border rounded" />
                </div>
                <div>
                    <label class="block text-sm">Téléphone</label>
                    <input id="modal-telephone" class="w-full p-2 border rounded" />
                </div>
                <div class="flex gap-2 justify-end mt-4">
                    <button id="btn-create-patient" class="px-4 py-2 bg-green-600 text-white rounded">Créer</button>
                    <button id="btn-close" class="px-4 py-2 bg-gray-200 rounded">Fermer</button>
                </div>
            </div>
        `;

            overlay.appendChild(box);

            // handlers
            overlay.querySelector('#btn-close').addEventListener('click', () => overlay.remove());
            overlay.querySelector('#btn-clear-ssn').addEventListener('click', () => {
                overlay.querySelector('#modal-ssn').value = '';
                overlay.querySelector('#modal-alert').textContent = '';
            });
                overlay.querySelector('#btn-verify-ssn').addEventListener('click', async () => {
                const verifyBtn = overlay.querySelector('#btn-verify-ssn');
                const ssn = overlay.querySelector('#modal-ssn').value.trim();
                if (!ssn) { overlay.querySelector('#modal-alert').textContent = 'Veuillez saisir un numéro de sécurité sociale.'; return; }
                overlay.querySelector('#modal-alert').textContent = 'Vérification en cours...';
                verifyBtn.disabled = true;
                const url = `${window.APP_CONTEXT}/api/patient?ssn=` + encodeURIComponent(ssn);
                console.debug('verify fetch url=', url);
                try {
                    const res = await fetch(url);
                    console.debug('verify response status=', res.status);
                    let data = null;
                    try {
                        data = await res.json();
                        console.debug('verify parsed json=', data);
                    } catch (parseErr) {
                        console.debug('verify parse error', parseErr);
                        overlay.querySelector('#modal-alert').textContent = 'Réponse invalide du serveur';
                        return;
                    }

                    // if server returned patient object (found=true), prefill regardless of HTTP status
                    if (data && data.found === true) {
                        console.debug('verify path: found (server)', data.fallback ? 'via fallback' : 'dao');
                        overlay.querySelector('#modal-nom').value = data.nom || '';
                        overlay.querySelector('#modal-prenom').value = data.prenom || '';
                        overlay.querySelector('#modal-email').value = data.email || '';
                        overlay.querySelector('#modal-telephone').value = data.telephone || '';
                        // prefill vitals if present
                        overlay.querySelector('#modal-temperature').value = (data.temperature !== undefined && data.temperature !== null) ? data.temperature : '';
                        overlay.querySelector('#modal-tension').value = data.tension || '';
                        overlay.querySelector('#modal-fc').value = (data.frequence_cardiaque !== undefined && data.frequence_cardiaque !== null) ? data.frequence_cardiaque : '';
                        overlay.querySelector('#modal-fr').value = (data.frequence_respiratoire !== undefined && data.frequence_respiratoire !== null) ? data.frequence_respiratoire : '';
                        overlay.querySelector('#modal-poids').value = (data.poids !== undefined && data.poids !== null) ? data.poids : '';
                        overlay.querySelector('#modal-taille').value = (data.taille !== undefined && data.taille !== null) ? data.taille : '';
                        overlay.querySelector('#modal-alert').textContent = 'Patient trouvé — champs pré-remplis.';
                    } else if (res.status === 404 || (data && data.found === false)) {
                        console.debug('verify path: not found');
                        overlay.querySelector('#modal-alert').textContent = 'Aucun patient trouvé. Remplissez les champs pour créer un nouveau patient.';
                    } else {
                        console.debug('verify path: server error', data);
                        // show server-provided error message if any
                        overlay.querySelector('#modal-alert').textContent = (data && (data.error || data.message)) || 'Erreur serveur';
                    }
                } catch (e) {
                    console.debug('verify network error', e);
                    overlay.querySelector('#modal-alert').textContent = 'Erreur réseau';
                } finally {
                    verifyBtn.disabled = false;
                }
            });

            overlay.querySelector('#btn-create-patient').addEventListener('click', async () => {
                const payload = {
                    numero_securite_sociale: overlay.querySelector('#modal-ssn').value.trim(),
                    nom: overlay.querySelector('#modal-nom').value.trim(),
                    prenom: overlay.querySelector('#modal-prenom').value.trim(),
                    email: overlay.querySelector('#modal-email').value.trim(),
                    telephone: overlay.querySelector('#modal-telephone').value.trim(),
                        password: 'azerty',
                        antecedents: overlay.querySelector('#modal-antecedents').value.trim(),
                        allergies: overlay.querySelector('#modal-allergies').value.trim(),
                        traitements_en_cours: overlay.querySelector('#modal-traitements').value.trim()
                };
                // basic client validation
                if (!payload.numero_securite_sociale || !payload.nom || !payload.prenom || !payload.email) {
                    overlay.querySelector('#modal-alert').textContent = 'Tous les champs obligatoires doivent être remplis.';
                    return;
                }
                overlay.querySelector('#modal-alert').textContent = 'Création en cours...';
                try {
                   // console.debug('create payload=', payload);
                    // prepare numeric values as numbers (or null)
                    const tempRaw = overlay.querySelector('#modal-temperature').value.trim();
                    const temperature = tempRaw === '' ? null : Number(tempRaw);
                    const tensionVal = overlay.querySelector('#modal-tension').value.trim() || null;
                    const fcRaw = overlay.querySelector('#modal-fc').value.trim();
                    const frequence_cardiaque = fcRaw === '' ? null : parseInt(fcRaw, 10);
                    const frRaw = overlay.querySelector('#modal-fr').value.trim();
                    const frequence_respiratoire = frRaw === '' ? null : parseInt(frRaw, 10);
                    const poidsRaw = overlay.querySelector('#modal-poids').value.trim();
                    const poids = poidsRaw === '' ? null : Number(poidsRaw);
                    const tailleRaw = overlay.querySelector('#modal-taille').value.trim();
                    const taille = tailleRaw === '' ? null : Number(tailleRaw);

                    const res = await fetch(`${window.APP_CONTEXT}/api/patient`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(Object.assign(payload, {
                            temperature: temperature,
                            tension: tensionVal,
                            frequence_cardiaque: frequence_cardiaque,
                            frequence_respiratoire: frequence_respiratoire,
                            poids: poids,
                            taille: taille
                        }))
                    });
                    console.debug('create response status=', res.status);
                    const body = await res.json();
                    console.debug('create response body=', body);
                    if (res.ok) {
                        overlay.querySelector('#modal-alert').textContent = 'Patient créé (ID: ' + body.id + ')';
                        // add to local list for demo
                        samplePatients.push({id: body.id, nom: payload.nom, prenom: payload.prenom, tel: payload.telephone});
                        renderPatients();
                        setTimeout(() => overlay.remove(), 1200);
                    } else {
                        overlay.querySelector('#modal-alert').textContent = body.error || 'Erreur création';
                    }
                } catch (e) {
                    console.debug('create network error', e);
                    overlay.querySelector('#modal-alert').textContent = 'Erreur réseau';
                }
            });

            return overlay;
        }
        
        const btnNew = document.getElementById('btn-new-patient');

        btnNew.addEventListener("click", () => {
            console.log("hhhh");
            openNewPatientModal();
        });

        function escapeHtml(s) {
            return String(s === null || s === undefined ? '' : s)
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;');
        }

        async function loadQueue() {
            const statusEl = document.getElementById('list-attend');
            const ul = document.getElementById('queue-list');
            const ts = document.getElementById('queue-timestamp');
            try {
                const url = window.APP_CONTEXT + '/api/generaliste/queue';
                const res = await fetch(url);
                if (!res.ok) {
                    const txt = await res.text().catch(()=>'<no body>');
                    statusEl.innerHTML = 'Erreur chargement file (status=' + res.status + ')<pre class="mt-2 text-xs text-red-600">' + escapeHtml(txt) + '</pre>';
                    ul.innerHTML = '';
                    ts.textContent = '—';
                    return;
                }
                const data = await res.json();
                // keep a copy for stats calculations
                queueEntries = Array.isArray(data) ? data : [];
                if (!Array.isArray(data) || data.length === 0) {
                    ul.innerHTML = '<li class="text-sm text-gray-600">Aucune personne en attente pour aujourd\'hui</li>';
                    ts.textContent = new Date().toLocaleString();
                    statusEl.textContent = 'Actualisé:';
                    return;
                }

                ul.innerHTML = data.map(function(item, idx){
                    const p = item.patient || {
                        id: item.patientId || '',
                        nom: item.nom || '',
                        prenom: item.prenom || '',
                        numero_securite_sociale: item.numero_securite_sociale || '',
                        telephone: item.telephone || ''
                    };
                    const time = item.createdAt ? new Date(item.createdAt).toLocaleTimeString() : '';
                    const queueId = item.id || item.queueId || '';
                    return '<li class="flex items-center justify-between" data-queue-id="' + escapeHtml(queueId) + '">'
                        + '<div>' + escapeHtml(p.nom||'') + ' ' + escapeHtml(p.prenom||'') + ' <span class="text-xs text-gray-500">(' + escapeHtml(p.numero_securite_sociale||'') + ')</span></div>'
                        + '<div class="flex gap-2">'
                        + '<button class="accept-btn px-2 py-1 bg-green-500 text-white rounded text-xs" data-queue-id="' + escapeHtml(queueId) + '">Prendre</button>'
                        + '<button class="reject-btn px-2 py-1 bg-red-500 text-white rounded text-xs" data-queue-id="' + escapeHtml(queueId) + '">Refuser</button>'
                        + '</div>'
                        + '</li>';
                }).join('');

                ts.textContent = new Date().toLocaleString();
                statusEl.textContent = 'Actualisé:';
                try { updateStats(); } catch (e) { /* updateStats may be defined later */ }

                ul.removeEventListener && ul.removeEventListener('click', ul._queueClickHandler);
                const handler = async function(ev){
                    const btn = ev.target.closest('button');
                    if (!btn) return;
                    const qid = btn.dataset.queueId;
                    if (!qid) return;
                    const action = btn.classList.contains('accept-btn') ? 'accept' : (btn.classList.contains('reject-btn') ? 'reject' : null);
                    if (!action) return;
                    if (action === 'reject') {
                        if (!confirm('Confirmer le refus de cette personne ?')) return;
                    }
                    const url2 = window.APP_CONTEXT + '/api/queue/' + encodeURIComponent(qid) + '/' + action;
                    try {
                        const r = await fetch(url2, { method: 'POST' });
                        if (!r.ok) {
                            const txt = await r.text().catch(()=>'');
                            alert('Erreur serveur: ' + r.status + '\n' + txt);
                            return;
                        }
                        const row = btn.closest('[data-queue-id]');
                        if (row) row.remove();
                    } catch (err) {
                        alert('Erreur réseau: ' + (err && err.message ? err.message : 'unknown'));
                    }
                };
                ul.addEventListener('click', handler);
                ul._queueClickHandler = handler;

            } catch (e) {
                statusEl.textContent = 'Erreur réseau';
                ul.innerHTML = '';
                console.debug('loadQueue network error', e);
            }
        }

        loadQueue();
    </script>

    <jsp:include page="/WEB-INF/includes/footer.jsp" />
</div>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    const tabs = {
        patients: document.getElementById('tab-patients'),
        queue: document.getElementById('tab-queue'),
        stats: document.getElementById('tab-stats')
    };
    const sections = {
        patients: document.getElementById('section-patients'),
        queue: document.getElementById('section-queue'),
        stats: document.getElementById('section-stats')
    };

    function showTab(name) {
        Object.keys(sections).forEach(k => {
            sections[k].classList.toggle('hidden', k !== name);
            tabs[k].classList.toggle('bg-blue-50', k === name);
        });
    }

    tabs.patients.addEventListener('click', () => showTab('patients'));
    tabs.queue.addEventListener('click', () => showTab('queue'));
    tabs.stats.addEventListener('click', () => showTab('stats'));

    let samplePatients = []; 
    let queueEntries = [];

    async function renderPatients() {
        const tbody = document.getElementById('patients-tbody');
        tbody.innerHTML = '';
        try {
            const res = await fetch(`${window.APP_CONTEXT}/api/patients`);
            if (!res.ok) {
                tbody.innerHTML = `<tr><td colspan="5" class="p-2 text-red-600">Erreur chargement patients (${res.status})</td></tr>`;
                return;
            }
            samplePatients = await res.json();
        } catch (e) {
            tbody.innerHTML = `<tr><td colspan="5" class="p-2 text-red-600">Erreur réseau</td></tr>`;
            console.debug('fetch patients error', e);
            return;
        }

        console.debug('patients fetched', samplePatients);
        samplePatients.forEach((p, i) => {
            const tr = document.createElement('tr');
            const nom = (typeof p.nom === 'string') ? p.nom : '';
            const prenom = (typeof p.prenom === 'string') ? p.prenom : '';
            const telephone = (typeof p.telephone === 'string') ? p.telephone : '';
            tr.innerHTML = `
                <td class="p-2">${i+1}</td>
                <td class="p-2">${nom}</td>
                <td class="p-2">${prenom}</td>
                <td class="p-2">${telephone}</td>
                <td class="p-2"><button data-id="${p.id}" class="open-patient-btn px-2 py-1 bg-green-600 text-white rounded">Ouvrir</button></td>
            `;
            tbody.appendChild(tr);
        });

        document.querySelectorAll('.open-patient-btn').forEach(b => b.addEventListener('click', (ev) => {
            const id = ev.currentTarget.getAttribute('data-id');
            console.debug('Open patient', id);
        }));
    }

    function renderQueue() {
        const list = document.getElementById('queue-list');
        list.innerHTML = '';
        samplePatients.slice(0,2).forEach((p, i) => {
            const li = document.createElement('li');
            li.className = 'flex items-center justify-between';
            li.innerHTML = `<div>${p.nom} ${p.prenom} <span class='text-gray-500 text-xs'>(${p.id})</span></div><div><button class='px-2 py-1 bg-blue-600 text-white rounded'>Prendre</button></div>`;
            list.appendChild(li);
        });
        document.getElementById('queue-timestamp').textContent = new Date().toLocaleString();
    }

    // chart
    const ctx = document.getElementById('chart-patients').getContext('2d');
    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'],
            datasets: [{
                label: 'Nouveaux patients',
                data: [5,8,6,10,7,4,9],
                borderColor: '#2563EB',
                backgroundColor: 'rgba(37,99,235,0.1)'
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    // update stats UI and chart
    function updateStats() {
        const total = Array.isArray(samplePatients) ? samplePatients.length : 0;
        document.getElementById('stat-total').textContent = total;

        const waiting = Array.isArray(queueEntries) ? queueEntries.length : 0;
        document.getElementById('stat-waiting').textContent = waiting;

        const today = new Date().toDateString();
        const patientIdsToday = new Set();
        if (Array.isArray(queueEntries)) {
            queueEntries.forEach(q => {
                try {
                    const d = q.createdAt ? new Date(q.createdAt) : null;
                    if (d && d.toDateString() === today) {
                        const pid = (q.patient && q.patient.id) || q.patientId || null;
                        if (pid) patientIdsToday.add(pid);
                    }
                } catch (e) { /* ignore parse errors */ }
            });
        }
        const consultsToday = patientIdsToday.size;
        document.getElementById('stat-today').textContent = consultsToday;

        try {
            chart.data.labels = ['Consultations aujourd\'hui', 'En attente'];
            chart.data.datasets = [{ label: 'Comptes', data: [consultsToday, waiting], backgroundColor: ['#10B981', '#F97316'] }];
            chart.update();
        } catch (e) { console.debug('chart update error', e); }
    }

    // initial render
    renderPatients().then(() => { try { updateStats(); } catch(e){} });
    loadQueue();
    showTab('patients');

    // populate stats numbers
    document.getElementById('stat-total').textContent = samplePatients.length;
    document.getElementById('stat-waiting').textContent = 2;
    document.getElementById('stat-today').textContent = 7;

    // Modal logic for new patient


    function openNewPatientModal() {
        const modal = createModal();
        document.body.appendChild(modal);
    }


</script>
</body>
</html>