/**
 * Notifications System — MD3 Edition
 * Split polling: Lightweight unread count every 5s, full data on click.
 */

document.addEventListener("DOMContentLoaded", () => {
    const notificationBtn = document.getElementById("notificationBtn");
    const notificationDropdown = document.getElementById("notificationDropdown");
    const notificationBadge = document.getElementById("notificationBadge");
    const notifList = document.getElementById("notifList");
    const markAllReadBtn = document.getElementById("markAllReadBtn");

    if (!notificationBtn) return;

    // 1. Initial count check + interval polling
    checkUnreadCount();
    setInterval(checkUnreadCount, 5000);

    // 2. Toggle dropdown + fetch full data
    notificationBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        const isOpen = notificationDropdown.classList.contains("show");
        
        // Close other dropdowns if any
        document.querySelectorAll(".dropdown-content, .notification-dropdown").forEach(d => d.classList.remove("show"));

        if (!isOpen) {
            notificationDropdown.classList.add("show");
            fetchNotifications();
        } else {
            notificationDropdown.classList.remove("show");
        }
    });

    // 3. Mark all as read
    markAllReadBtn.addEventListener("click", async (e) => {
        e.stopPropagation();
        try {
            const res = await fetch("/api/notifications/mark-read", { method: "POST" });
            if (res.ok) {
                updateBadge(0);
                fetchNotifications();
                App.showToast("All notifications marked as read", "success");
            }
        } catch (err) {
            console.error("Failed to mark read:", err);
        }
    });

    // Close on outside click
    document.addEventListener("click", (e) => {
        if (!notificationDropdown.contains(e.target) && !notificationBtn.contains(e.target)) {
            notificationDropdown.classList.remove("show");
        }
    });

    async function checkUnreadCount() {
        try {
            const res = await fetch("/api/notifications/unread-count");
            if (res.status === 401) return;
            const data = await res.json();
            updateBadge(data.unreadCount);
        } catch (err) {
            // Silently fail polling
        }
    }

    async function fetchNotifications() {
        notifList.innerHTML = `
            <div class="notif-loading" style="display:flex; justify-content:center; padding: 30px;">
                <md-circular-progress indeterminate style="--md-circular-progress-size: 32px;"></md-circular-progress>
            </div>`;
        try {
            const res = await fetch("/api/notifications");
            if (!res.ok) throw new Error("Failed to fetch");
            const notifications = await res.json();
            renderNotifications(notifications);
        } catch (err) {
            notifList.innerHTML = `
                <div class="notif-error">
                    <md-icon style="font-size: 2rem; display: block; margin-bottom: 8px; opacity: 0.4;">error_outline</md-icon>
                    Failed to load notifications
                </div>`;
        }
    }

    function renderNotifications(notifications) {
        if (!notifications || notifications.length === 0) {
            notifList.innerHTML = `
                <div class="notif-empty">
                    <md-icon style="font-size: 2rem; display: block; margin-bottom: 8px; opacity: 0.4;">notifications_none</md-icon>
                    No notifications yet
                </div>`;
            return;
        }

        notifList.innerHTML = notifications.map(n => {
            const iconName = n.type === 'LIKE' ? 'favorite' : 
                             n.type === 'FOLLOW' ? 'person_add' : 
                             'chat_bubble';
            const iconColor = n.type === 'LIKE' ? 'var(--danger-color)' : 
                              n.type === 'FOLLOW' ? 'var(--accent-color)' : 
                              'var(--success-color)';
            
            const message = n.type === 'LIKE' ? `liked your prompt <strong>${n.promptTitle}</strong>` :
                            n.type === 'FOLLOW' ? `started following you` :
                            `commented on <strong>${n.promptTitle}</strong>`;

            const link = n.type === 'FOLLOW' ? `/users/${n.actorName}` : `/prompts/${n.promptId}`;

            return `
                <a href="${link}" class="notif-item ${n.read ? '' : 'unread'}">
                    <div class="notif-avatar">
                        ${n.actorAvatar ? `<img src="${n.actorAvatar}" alt="">` : `<div class="avatar-stub">${n.actorName.charAt(0).toUpperCase()}</div>`}
                    </div>
                    <div class="notif-content">
                        <p><strong>${n.actorName}</strong> ${message}</p>
                        <span class="notif-time"><md-icon style="font-size:14px; color:${iconColor};">${iconName}</md-icon> ${n.relativeTime}</span>
                    </div>
                </a>
            `;
        }).join('');
    }

    function updateBadge(count) {
        if (count > 0) {
            notificationBadge.textContent = count > 99 ? "99+" : count;
            notificationBadge.style.display = "flex";
        } else {
            notificationBadge.style.display = "none";
        }
    }
});
