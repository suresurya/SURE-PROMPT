let currentPage = 0;
let currentTab = "all";
let isLast = false;
let isLoading = false;
const loadedIds = new Set();

function copyPrompt(text) {
    navigator.clipboard.writeText(text);
    alert("Prompt copied to clipboard!");
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
            document.getElementById("feedContainer").innerHTML = "<p style='text-align:center; padding:2rem;'>⚠️ Failed to load feed. Please refresh.</p>";
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
        container.innerHTML = "<p style='text-align:center; padding:2rem;'>No prompts found.</p>";
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
        card.innerHTML = `
            <a href="/prompts/${p.id}">
                <h2>${p.title}</h2>
                <p>${p.promptBody}</p>
            </a>
            <div style="margin-top: 1rem; display: flex; gap: 1rem; align-items: center;">
                <button class="like-btn" data-id="${p.id}" style="background:none; border:none; cursor:pointer; font-size:1.2rem;">
                    ${p.liked ? "❤️" : "🤍"}
                </button>
                <span>${p.likeCount}</span>
                <button class="save-btn" data-id="${p.id}" style="background:none; border:none; cursor:pointer; font-size:1.2rem;">
                    ${p.saved ? "🔖" : "📑"}
                </button>
            </div>
        `;
        fragment.appendChild(card);
    });

    container.appendChild(fragment);
}

function switchTab(tab, btn) {
    if (currentTab === tab && currentPage === 0) return;

    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
    else document.querySelector(`.tab-btn[data-tab="${tab}"]`)?.classList.add('active');

    history.pushState({ tab }, "", `?tab=${tab}`);
    loadFeed(tab, 0);
}

// Social Actions with Event Delegation and Double-click protection
document.addEventListener("click", async (e) => {
    const likeBtn = e.target.closest(".like-btn");
    const saveBtn = e.target.closest(".save-btn");

    if (likeBtn) {
        if (likeBtn.disabled) return;
        likeBtn.disabled = true;
        const id = likeBtn.dataset.id;
        try {
            const res = await fetch(`/api/prompts/${id}/like`, { method: 'POST' });
            const data = await res.json();
            likeBtn.innerText = data.liked ? "❤️" : "🤍";
            likeBtn.nextElementSibling.innerText = data.likeCount;
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
            const data = await res.json();
            saveBtn.innerText = data.saved ? "🔖" : "📑";
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
        // Initial SSR load already present, but hydrate the IDs
        document.querySelectorAll('.prompt-card').forEach(card => {
            const link = card.querySelector('a');
            if (link) {
                const id = link.getAttribute('href').split('/').pop();
                loadedIds.add(parseInt(id));
            }
        });
    }
});
