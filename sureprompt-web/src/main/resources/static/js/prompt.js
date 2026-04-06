// =========================================================================
// Feed — Prompt loading, tab switching, infinite scroll
// MD3 compatible
// =========================================================================

let currentPage = 0;
let currentTab = "all";
let isLast = false;
let isLoading = false;
const loadedIds = new Set();

function copyPrompt(text) {
    navigator.clipboard.writeText(text);
    App.showToast("Prompt copied to clipboard!", "success");
}

async function loadFeed(tab, page = 0, retry = 1) {
    if (isLoading) return;
    isLoading = true;
    currentTab = tab;
    currentPage = page;

    if (page === 0) {
        showSkeletons();
        loadedIds.clear();
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    try {
        const res = await fetch(`/api/feed?tab=${tab}&page=${page}`);
        if (!res.ok) throw new Error("Failed to fetch");
        const data = await res.json();

        isLast = data.last;
        if (page === 0) {
            renderFeed(data.content);
        } else {
            appendFeed(data.content);
        }
    } catch (err) {
        if (retry > 0) {
            isLoading = false;
            return loadFeed(tab, page, retry - 1);
        } else {
            document.getElementById("feedContainer").innerHTML = `
                <div style="text-align:center; padding:2rem; color: var(--text-muted);">
                    <md-icon style="font-size: 2rem; display: block; margin-bottom: 8px;">wifi_off</md-icon>
                    Failed to load feed. Please refresh.
                </div>`;
        }
    } finally {
        isLoading = false;
    }
}

function showSkeletons() {
    const container = document.getElementById("feedContainer");
    let html = "";
    for (let i = 0; i < 5; i++) {
        html += `<div class="skeleton-card"></div>`;
    }
    container.innerHTML = html;
}

function renderFeed(prompts) {
    const container = document.getElementById("feedContainer");
    container.innerHTML = "";
    if (prompts.length === 0) {
        container.innerHTML = `
            <div style="text-align:center; padding: 3rem; color: var(--text-muted);">
                <md-icon style="font-size: 3rem; display: block; margin-bottom: 12px; opacity: 0.4;">edit_note</md-icon>
                No prompts found.
            </div>`;
        return;
    }
    appendFeed(prompts);
}

function appendFeed(prompts) {
    const container = document.getElementById("feedContainer");
    const fragment = document.createDocumentFragment();

    prompts.forEach(p => {
        if (loadedIds.has(p.id)) return;
        loadedIds.add(p.id);

        const card = document.createElement("div");
        card.className = "prompt-card";
        card.style.cssText = "background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--border-radius-lg); padding: 24px; margin-bottom: 1.5rem; position: relative; overflow: hidden; transition: all 0.3s ease;";
        card.innerHTML = `
            <md-ripple></md-ripple>
            <a href="/prompts/${p.id}" style="text-decoration:none; color:inherit; display:block;">
                <h2 style="font-size:1.3rem; font-weight:700; margin-bottom:0.5rem;">${p.title}</h2>
                <p style="color:var(--text-muted); font-size:0.95rem; line-height:1.5; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">${p.promptBody}</p>
            </a>
            <div style="margin-top: 1rem; display: flex; gap: 0.5rem; align-items: center;">
                <md-icon-button class="like-btn" data-id="${p.id}">
                    <md-icon${p.liked ? ' style="font-variation-settings: \'FILL\' 1; color: var(--danger-color);"' : ''}>favorite</md-icon>
                </md-icon-button>
                <span style="color: var(--text-muted); font-size: 0.9rem;">${p.likeCount}</span>
                
                <md-icon-button class="save-btn" data-id="${p.id}">
                    <md-icon${p.saved ? ' style="font-variation-settings: \'FILL\' 1; color: var(--accent-color);"' : ''}>bookmark</md-icon>
                </md-icon-button>
            </div>
        `;
        fragment.appendChild(card);
    });

    container.appendChild(fragment);
}

function switchTab(tab, btn) {
    if (currentTab === tab && currentPage === 0) return;
    history.pushState({ tab }, "", `?tab=${tab}`);
    loadFeed(tab, 0);
}

// Social Actions with Event Delegation
document.addEventListener("click", async (e) => {
    const likeBtn = e.target.closest("md-icon-button.like-btn, .like-btn");
    const saveBtn = e.target.closest("md-icon-button.save-btn, .save-btn");

    if (likeBtn) {
        if (likeBtn.disabled) return;
        likeBtn.disabled = true;
        const id = likeBtn.dataset.id;
        try {
            const res = await fetch(`/api/prompts/${id}/like`, { method: 'POST' });
            if (res.status === 401) { window.location.href = '/login'; return; }
            const data = await res.json();
            const icon = likeBtn.querySelector('md-icon');
            if (icon) {
                if (data.liked) {
                    icon.style.fontVariationSettings = "'FILL' 1";
                    icon.style.color = "var(--danger-color)";
                } else {
                    icon.style.fontVariationSettings = "'FILL' 0";
                    icon.style.color = "";
                }
            }
            const countEl = likeBtn.nextElementSibling;
            if (countEl && countEl.tagName !== 'MD-ICON-BUTTON') {
                countEl.textContent = data.likeCount;
            }
        } finally {
            likeBtn.disabled = false;
        }
    }

    if (saveBtn) {
        if (saveBtn.disabled) return;
        saveBtn.disabled = true;
        const id = saveBtn.dataset.id;
        try {
            const res = await fetch(`/api/prompts/${id}/save`, { method: 'POST' });
            if (res.status === 401) { window.location.href = '/login'; return; }
            const data = await res.json();
            const icon = saveBtn.querySelector('md-icon');
            if (icon) {
                if (data.saved) {
                    icon.style.fontVariationSettings = "'FILL' 1";
                    icon.style.color = "var(--accent-color)";
                } else {
                    icon.style.fontVariationSettings = "'FILL' 0";
                    icon.style.color = "";
                }
            }
        } finally {
            saveBtn.disabled = false;
        }
    }
});

// Infinite Scroll
window.addEventListener("scroll", () => {
    if (isLoading || isLast) return;
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
        loadFeed(currentTab, currentPage + 1);
    }
});

// History Handling
window.onpopstate = (e) => {
    const params = new URLSearchParams(window.location.search);
    const tab = params.get("tab") || "all";
    switchTab(tab);
};

// Initial Load Handling
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const tab = params.get("tab");
    if (tab && tab !== "all") {
        switchTab(tab);
    } else {
        document.querySelectorAll('.prompt-card').forEach(card => {
            const link = card.querySelector('a');
            if (link) {
                const href = link.getAttribute('href');
                if (href) {
                    const id = href.split('/').pop();
                    loadedIds.add(parseInt(id));
                }
            }
        });
    }
});
