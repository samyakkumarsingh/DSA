const API_BASE = '/api';

let allProblems = [];

// ===== Initialize =====
document.addEventListener('DOMContentLoaded', () => {
    loadProblems();
    loadStats();

    document.getElementById('add-problem-form').addEventListener('submit', handleAddProblem);
    document.getElementById('filter-topic').addEventListener('change', applyFilters);
    document.getElementById('filter-difficulty').addEventListener('change', applyFilters);
    document.getElementById('filter-status').addEventListener('change', applyFilters);
});

// ===== API Functions =====
async function loadProblems() {
    try {
        const response = await fetch(`${API_BASE}/problems`);
        allProblems = await response.json();
        renderProblems(allProblems);
    } catch (error) {
        console.error('Failed to load problems:', error);
    }
}

async function loadStats() {
    try {
        const response = await fetch(`${API_BASE}/stats`);
        const stats = await response.json();
        document.getElementById('stat-total').textContent = stats.total;
        document.getElementById('stat-solved').textContent = stats.solved;
        document.getElementById('stat-attempted').textContent = stats.attempted;
        document.getElementById('stat-pending').textContent = stats.pending;
        document.getElementById('stat-easy').textContent = stats.easy;
        document.getElementById('stat-medium').textContent = stats.medium;
        document.getElementById('stat-hard').textContent = stats.hard;
    } catch (error) {
        console.error('Failed to load stats:', error);
    }
}

async function handleAddProblem(e) {
    e.preventDefault();

    const problem = {
        title: document.getElementById('title').value.trim(),
        topic: document.getElementById('topic').value,
        difficulty: document.getElementById('difficulty').value,
        status: document.getElementById('status').value,
        link: document.getElementById('link').value.trim(),
        notes: document.getElementById('notes').value.trim()
    };

    if (!problem.title || !problem.topic) {
        showToast('Please fill in all required fields', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/problems`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(problem)
        });

        if (response.ok) {
            showToast('Problem added successfully!', 'success');
            document.getElementById('add-problem-form').reset();
            loadProblems();
            loadStats();
        } else {
            showToast('Failed to add problem', 'error');
        }
    } catch (error) {
        showToast('Server error. Is the backend running?', 'error');
    }
}

async function deleteProblem(id) {
    if (!confirm('Are you sure you want to delete this problem?')) return;

    try {
        const response = await fetch(`${API_BASE}/problems/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Problem deleted', 'success');
            loadProblems();
            loadStats();
        } else {
            showToast('Failed to delete problem', 'error');
        }
    } catch (error) {
        showToast('Server error', 'error');
    }
}

async function toggleStatus(id) {
    const problem = allProblems.find(p => p.id === id);
    if (!problem) return;

    const statusCycle = { 'Pending': 'Attempted', 'Attempted': 'Solved', 'Solved': 'Pending' };
    const newStatus = statusCycle[problem.status] || 'Pending';

    try {
        const response = await fetch(`${API_BASE}/problems/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });

        if (response.ok) {
            showToast(`Status changed to ${newStatus}`, 'success');
            loadProblems();
            loadStats();
        }
    } catch (error) {
        showToast('Server error', 'error');
    }
}

// ===== Rendering =====
function renderProblems(problems) {
    const tbody = document.getElementById('problems-body');

    if (problems.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-row">
                <td colspan="7">No problems found. Start tracking your DSA journey!</td>
            </tr>`;
        return;
    }

    tbody.innerHTML = problems.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>
                ${p.link ? `<a href="${escapeHtml(p.link)}" target="_blank" rel="noopener noreferrer" class="problem-link">${escapeHtml(p.title)}</a>` : escapeHtml(p.title)}
            </td>
            <td>${escapeHtml(p.topic)}</td>
            <td><span class="badge badge-${p.difficulty.toLowerCase()}">${escapeHtml(p.difficulty)}</span></td>
            <td><span class="badge badge-${p.status.toLowerCase()}">${escapeHtml(p.status)}</span></td>
            <td>${escapeHtml(p.notes || '')}</td>
            <td>
                <div class="actions">
                    <button class="btn btn-success" onclick="toggleStatus(${p.id})" title="Cycle status">✓</button>
                    <button class="btn btn-danger" onclick="deleteProblem(${p.id})" title="Delete">✕</button>
                </div>
            </td>
        </tr>
    `).join('');
}

// ===== Filters =====
function applyFilters() {
    const topic = document.getElementById('filter-topic').value;
    const difficulty = document.getElementById('filter-difficulty').value;
    const status = document.getElementById('filter-status').value;

    let filtered = allProblems;
    if (topic) filtered = filtered.filter(p => p.topic === topic);
    if (difficulty) filtered = filtered.filter(p => p.difficulty === difficulty);
    if (status) filtered = filtered.filter(p => p.status === status);

    renderProblems(filtered);
}

// ===== Utilities =====
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}
