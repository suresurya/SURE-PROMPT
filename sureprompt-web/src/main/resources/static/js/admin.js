/**
 * SurePrompt Admin Panel Logic
 * Handles moderation actions: resolve, reject, ban
 */

async function resolveReport(reportId, action) {
    const noteInput = document.getElementById(`note-${reportId}`);
    const note = noteInput ? noteInput.value.trim() : "";

    if (!note) {
        alert("Please provide an admin note (mandatory for audit).");
        noteInput.focus();
        return;
    }

    if (!confirm(`Are you sure you want to resolve this report with action: ${action}?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/reports/${reportId}/resolve`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action, note })
        });

        if (response.ok) {
            showToast("Action applied successfully", "success");
            const card = document.getElementById(`report-${reportId}`);
            if (card) {
                card.style.opacity = '0.5';
                card.style.pointerEvents = 'none';
                card.innerHTML = `<div class="resolved-overlay"><i class="fas fa-check-circle"></i> Resolved: ${action}</div>`;
            }
        } else {
            const error = await response.json();
            showToast(error.message || "Failed to resolve report", "error");
        }
    } catch (err) {
        showToast("Network error occurred", "error");
    }
}

async function rejectReport(reportId) {
    const noteInput = document.getElementById(`note-${reportId}`);
    const note = noteInput ? noteInput.value.trim() : "";

    if (!note) {
        alert("Please provide a reason for rejection.");
        noteInput.focus();
        return;
    }

    try {
        const response = await fetch(`/api/admin/reports/${reportId}/reject`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ note })
        });

        if (response.ok) {
            showToast("Report rejected", "info");
            const card = document.getElementById(`report-${reportId}`);
            if (card) {
                card.style.opacity = '0.5';
                card.style.pointerEvents = 'none';
                card.innerHTML = `<div class="rejected-overlay"><i class="fas fa-times-circle"></i> Rejected</div>`;
            }
        } else {
            showToast("Failed to reject report", "error");
        }
    } catch (err) {
        showToast("Error occurred", "error");
    }
}

async function toggleUserBan(userId, btn) {
    const isBanning = btn.innerText === "Ban";
    if (!confirm(`Are you sure you want to ${isBanning ? 'ban' : 'unban'} this user?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/users/${userId}/ban`, {
            method: 'POST'
        });

        if (response.ok) {
            location.reload(); // Simple reload to update indicators
        } else {
            showToast("Action failed", "error");
        }
    } catch (err) {
        showToast("Error occurred", "error");
    }
}

/** Utility Toast */
function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerText = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// User Search logic
const userSearchInput = document.getElementById('userSearch');
if (userSearchInput) {
    userSearchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        const rows = document.querySelectorAll('#userTableBody tr');
        rows.forEach(row => {
            const text = row.innerText.toLowerCase();
            row.style.display = text.includes(term) ? '' : 'none';
        });
    });
}
