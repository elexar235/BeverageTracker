const API_URL = '';

let beverageCatalog = [];

document.addEventListener('DOMContentLoaded', () => {
    renderAuthStatus();
    loadBeverageTypes();
    loadUsers();
    loadLeaderboard();
    loadAchievements();

    document.getElementById('amount').addEventListener('input', updateEffectivePreview);
    document.getElementById('beverageTypeId').addEventListener('change', updateEffectivePreview);
});

function getAuthSession() {
    const raw = localStorage.getItem('bt_auth');
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch {
        return null;
    }
}

function setAuthSession(authData) {
    localStorage.setItem('bt_auth', JSON.stringify(authData));
    renderAuthStatus();
    loadUsers();
}

function logout() {
    localStorage.removeItem('bt_auth');
    renderAuthStatus();
}

function getAuthHeaders() {
    const session = getAuthSession();
    const headers = { 'Content-Type': 'application/json' };
    if (session && session.token) {
        headers['Authorization'] = `Bearer ${session.token}`;
    }
    return headers;
}

function renderAuthStatus() {
    const box = document.getElementById('authStatusBox');
    const session = getAuthSession();

    if (session && session.token) {
        const isAdmin = session.role === 'ROLE_ADMIN';
        box.innerHTML = `
            <div class="logged-in-pill">
                <span>👤 <strong>${session.username}</strong></span>
                <span class="role-badge ${isAdmin ? 'admin' : 'user'}">${session.role}</span>
                <span class="goal-tag">🎯 ${session.dailyWaterGoalMl || 2000} ml/day</span>
                <button onclick="logout()" class="btn-secondary btn-sm">Sign Out</button>
            </div>
        `;
    } else {
        box.innerHTML = `
            <div class="logged-out-pill">
                <span>🔓 Guest Mode (Sign in for JWT / Admin actions)</span>
            </div>
        `;
    }
}

// --- AUTHENTICATION ---
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        username: document.getElementById('loginUsername').value.trim(),
        password: document.getElementById('loginPassword').value
    };

    try {
        const res = await fetch(`${API_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            const authData = await res.json();
            setAuthSession(authData);
            document.getElementById('loginForm').reset();
        } else {
            const msg = await res.text();
            alert(msg || 'Invalid credentials');
        }
    } catch (err) {
        console.error(err);
        alert('Login request failed');
    }
});

async function quickAdminLogin() {
    document.getElementById('loginUsername').value = 'admin';
    document.getElementById('loginPassword').value = 'admin123';
    document.getElementById('loginForm').requestSubmit();
}

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        username: document.getElementById('regUsername').value.trim(),
        email: document.getElementById('regEmail').value.trim(),
        password: document.getElementById('regPassword').value,
        dailyWaterGoalMl: parseInt(document.getElementById('regGoal').value, 10) || 2000
    };

    try {
        const res = await fetch(`${API_URL}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            const authData = await res.json();
            setAuthSession(authData);
            document.getElementById('registerForm').reset();
        } else {
            const msg = await res.text();
            alert(msg || 'Registration failed');
        }
    } catch (err) {
        console.error(err);
        alert('Registration request failed');
    }
});

// --- BEVERAGE TYPES ---
async function loadBeverageTypes() {
    const select = document.getElementById('beverageTypeId');
    const catalogList = document.getElementById('beverageTypesList');

    try {
        const res = await fetch(`${API_URL}/beverage-types`);
        beverageCatalog = await res.json();

        select.innerHTML = beverageCatalog.map(b => `
            <option value="${b.id}" data-factor="${b.hydrationFactor}">
                ${b.icon || '🥤'} ${b.name} (${b.hydrationFactor}x hydration)
            </option>
        `).join('');

        catalogList.innerHTML = beverageCatalog.map(b => `
            <div class="beverage-chip">
                <span class="bev-icon">${b.icon || '🥤'}</span>
                <div class="bev-info">
                    <strong>${b.name}</strong>
                    <small>Hydration factor: ${(b.hydrationFactor * 100).toFixed(0)}%</small>
                </div>
            </div>
        `).join('');

        updateEffectivePreview();
    } catch (err) {
        console.error(err);
        catalogList.innerHTML = '<p class="error">Failed to load beverage types</p>';
    }
}

function setAmount(ml) {
    document.getElementById('amount').value = ml;
    updateEffectivePreview();
}

function updateEffectivePreview() {
    const amount = parseInt(document.getElementById('amount').value, 10) || 0;
    const select = document.getElementById('beverageTypeId');
    const selectedOpt = select.options[select.selectedIndex];
    const factor = selectedOpt ? parseFloat(selectedOpt.getAttribute('data-factor') || '1.0') : 1.0;
    const effective = Math.round(amount * factor);
    document.getElementById('effectivePreview').textContent = `Effective hydration: ${effective} ml`;
}

// --- LOG CONSUMPTION ---
document.getElementById('addConsumptionForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        userId: parseInt(document.getElementById('userId').value, 10),
        beverageTypeId: parseInt(document.getElementById('beverageTypeId').value, 10),
        amountMl: parseInt(document.getElementById('amount').value, 10)
    };

    try {
        const response = await fetch(`${API_URL}/consumptions`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });
        if (response.ok) {
            const logged = await response.json();
            alert(`Logged ${logged.amountMl} ml of ${logged.beverageName} (Effective hydration: ${logged.effectiveHydrationMl} ml) 💧`);
            loadLeaderboard();
            loadUsers();
        } else {
            const errorMsg = await response.text();
            alert(errorMsg || 'Error logging beverage');
        }
    } catch (err) {
        console.error(err);
    }
});

// --- ADMIN: CREATE BEVERAGE TYPE ---
document.getElementById('addBeverageForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        name: document.getElementById('bevName').value.trim(),
        hydrationFactor: parseFloat(document.getElementById('bevFactor').value),
        icon: document.getElementById('bevIcon').value.trim() || '🥤'
    };

    try {
        const res = await fetch(`${API_URL}/beverage-types`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            document.getElementById('addBeverageForm').reset();
            loadBeverageTypes();
        } else if (res.status === 401 || res.status === 403) {
            alert('Access Denied (403): Please sign in as Admin (ROLE_ADMIN) first!');
        } else {
            const msg = await res.text();
            alert(msg || 'Failed to create beverage type');
        }
    } catch (err) {
        console.error(err);
    }
});

// --- ADMIN: CREATE ACHIEVEMENT ---
document.getElementById('addAchievementForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        name: document.getElementById('achName').value.trim(),
        description: document.getElementById('achDesc').value.trim(),
        points: parseInt(document.getElementById('achPoints').value, 10),
        requiredConsumptions: parseInt(document.getElementById('achRequired').value, 10)
    };

    try {
        const res = await fetch(`${API_URL}/achievements`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            document.getElementById('addAchievementForm').reset();
            loadAchievements();
        } else if (res.status === 401 || res.status === 403) {
            alert('Access Denied (403): Please sign in as Admin (ROLE_ADMIN) first!');
        } else {
            const msg = await res.text();
            alert(msg || 'Failed to create achievement');
        }
    } catch (err) {
        console.error(err);
    }
});

// --- ACHIEVEMENTS LIST ---
async function loadAchievements() {
    const list = document.getElementById('achievementsList');
    list.innerHTML = '<p>Loading...</p>';

    try {
        const res = await fetch(`${API_URL}/achievements`);
        const achievements = await res.json();

        if (achievements.length === 0) {
            list.innerHTML = '<p>No achievements defined yet.</p>';
            return;
        }

        list.innerHTML = achievements.map(a => `
            <div class="user-item" style="border: 1px solid #38bdf8;">
                <h3 style="color: #38bdf8;">🏅 ${a.name}</h3>
                <p>${a.description || 'No description'}</p>
                <div style="margin-top: 0.5rem;">
                    <span class="id-badge">⭐ ${a.points || 0} pts</span>
                    <span class="id-badge">🥤 ${a.requiredConsumptions || 1} drinks</span>
                </div>
            </div>
        `).join('');
    } catch (err) {
        list.innerHTML = '<p class="error">Failed to load achievements</p>';
    }
}

// --- USERS LIST ---
async function loadUsers() {
    const list = document.getElementById('usersList');
    const userSelect = document.getElementById('userId');
    list.innerHTML = '<p>Loading...</p>';

    try {
        const res = await fetch(`${API_URL}/users`);
        const users = await res.json();
        const session = getAuthSession();

        userSelect.innerHTML = users.map(u => `
            <option value="${u.id}" ${session && session.userId === u.id ? 'selected' : ''}>
                #${u.id} — ${u.username} (Goal: ${u.dailyWaterGoalMl || 2000} ml)
            </option>
        `).join('');

        list.innerHTML = users.map(user => `
            <div class="user-item">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <span class="id-badge">ID: ${user.id}</span>
                    <span class="role-badge ${user.role === 'ROLE_ADMIN' ? 'admin' : 'user'}">${user.role || 'ROLE_USER'}</span>
                </div>
                <h3>${user.username}</h3>
                <p>${user.email}</p>
                <p style="margin-top:0.35rem; color:#38bdf8; font-weight:600;">🎯 Goal: ${user.dailyWaterGoalMl || 2000} ml/day</p>

                <div class="achievements" style="margin-top:0.5rem;">
                    ${(user.achievements || []).map(ach => `<span class="achievement-badge">🏅 ${ach}</span>`).join('')}
                </div>

                <button onclick="deleteUser(${user.id})" class="btn-danger">Delete 🗑️</button>
            </div>
        `).join('');
    } catch (err) {
        list.innerHTML = '<p class="error">Failed to load users</p>';
    }
}

async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) return;

    try {
        const response = await fetch(`${API_URL}/users/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (response.ok) {
            loadUsers();
            loadLeaderboard();
        } else {
            alert('Failed to delete user');
        }
    } catch (err) {
        console.error(err);
    }
}

// --- LEADERBOARD ---
async function loadLeaderboard() {
    const tbody = document.querySelector('#leaderboardTable tbody');
    tbody.innerHTML = '<tr><td colspan="3">Loading...</td></tr>';

    try {
        const res = await fetch(`${API_URL}/consumptions/stats/leaderboard`);
        const stats = await res.json();

        if (stats.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3">No beverage entries logged yet. Be the first!</td></tr>';
            return;
        }

        tbody.innerHTML = stats.map((stat, index) => `
            <tr>
                <td>#${index + 1}${index === 0 ? ' 👑' : ''}</td>
                <td><strong>${stat.username}</strong></td>
                <td>${(stat.totalAmountMl / 1000).toFixed(2)} L (${stat.totalAmountMl} ml)</td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="3">Failed to load leaderboard</td></tr>';
    }
}
