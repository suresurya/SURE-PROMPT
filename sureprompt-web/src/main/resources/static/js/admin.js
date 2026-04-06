/**
 * SurePrompt Admin Panel Logic — MD3 Edition
 * Handles moderation actions: resolve, reject, ban
 * Uses md-dialog for confirmations instead of native confirm()
 */

async function resolveReport(reportId, action) {
    // Get note from md-outlined-text-field
    const noteField = document.getElementById(`note-${reportId}`);
    const note = noteField ? noteField.value.trim() : "";

    if (!note) {
        App.showToast("Please provide an admin note (mandatory for audit).", "error");
        noteField?.focus();
        return;
    }

    // Use MD3 dialog for confirmation
    const confirmed = await showConfirmDialog(
        `Resolve Report`, 
        `Are you sure you want to resolve this report with action: ${action}?`
    );
    if (!confirmed) return;

    try {
        const response = await fetch(`/api/admin/reports/${reportId}/resolve`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action, note })
        });

        if (response.ok) {
            App.showToast("Action applied successfully", "success");
            const card = document.getElementById(`report-${reportId}`);
            if (card) {
                card.style.opacity = '0.5';
                card.style.pointerEvents = 'none';
                card.innerHTML = `<div class="resolved-overlay"><md-icon>check_circle</md-icon> Resolved: ${action}</div>`;
            }
        } else {
            const error = await response.json();
            App.showToast(error.message || "Failed to resolve report", "error");
        }
    } catch (err) {
        App.showToast("Network error occurred", "error");
    }
}

async function rejectReport(reportId) {
    const noteField = document.getElementById(`note-${reportId}`);
    const note = noteField ? noteField.value.trim() : "";

    if (!note) {
        App.showToast("Please provide a reason for rejection.", "error");
        noteField?.focus();
        return;
    }

    try {
        const response = await fetch(`/api/admin/reports/${reportId}/reject`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ note })
        });

        if (response.ok) {
            App.showToast("Report rejected", "info");
            const card = document.getElementById(`report-${reportId}`);
            if (card) {
                card.style.opacity = '0.5';
                card.style.pointerEvents = 'none';
                card.innerHTML = `<div class="rejected-overlay"><md-icon>cancel</md-icon> Rejected</div>`;
            }
        } else {
            App.showToast("Failed to reject report", "error");
        }
    } catch (err) {
        App.showToast("Error occurred", "error");
    }
}

async function toggleUserBan(userId, btn) {
    const isBanning = btn.textContent.trim().includes("Ban") && !btn.textContent.trim().includes("Unban");
    
    const confirmed = await showConfirmDialog(
        `${isBanning ? 'Ban' : 'Unban'} User`,
        `Are you sure you want to ${isBanning ? 'ban' : 'unban'} this user?`
    );
    if (!confirmed) return;

    try {
        const response = await fetch(`/api/admin/users/${userId}/ban`, {
            method: 'POST'
        });

        if (response.ok) {
            App.showToast(`User ${isBanning ? 'banned' : 'unbanned'} successfully`, "success");
            setTimeout(() => location.reload(), 800);
        } else {
            App.showToast("Action failed", "error");
        }
    } catch (err) {
        App.showToast("Error occurred", "error");
    }
}

/**
 * MD3 Dialog-based confirmation (replaces native confirm())
 * Returns a promise that resolves to true/false
 */
function showConfirmDialog(headline, content) {
    return new Promise((resolve) => {
        const dialog = document.getElementById('confirmDialog') || document.getElementById('banDialog');
        if (!dialog) {
            // Fallback to native confirm if no dialog element
            resolve(confirm(content));
            return;
        }

        const headlineEl = dialog.querySelector('[slot="headline"]');
        const contentEl = dialog.querySelector('[slot="content"]');
        const confirmBtn = dialog.querySelector('#confirmAction, #banDialogConfirm');

        if (headlineEl) headlineEl.textContent = headline;
        if (contentEl) contentEl.textContent = content;

        // Clear previous listeners
        const newConfirmBtn = confirmBtn.cloneNode(true);
        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

        newConfirmBtn.addEventListener('click', () => {
            dialog.close();
            resolve(true);
        });

        dialog.addEventListener('close', () => {
            resolve(false);
        }, { once: true });

        dialog.show();
    });
}

// User Search logic — works with MD3 text field
document.addEventListener('DOMContentLoaded', () => {
    const userSearchField = document.getElementById('userSearch');
    if (userSearchField) {
        // MD3 outlined-text-field fires 'input' event
        userSearchField.addEventListener('input', (e) => {
            const term = (e.target.value || '').toLowerCase();
            const rows = document.querySelectorAll('#userTableBody tr');
            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                row.style.display = text.includes(term) ? '' : 'none';
            });
        });
    }
});
