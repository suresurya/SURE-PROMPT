// =========================================================================
// SurePrompt interactions: feed, detail, and social actions
// =========================================================================

(() => {
    const state = {
        currentPage: 0,
        currentTab: "all",
        isLast: false,
        isLoading: false,
        loadedIds: new Set()
    };

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/\"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function formatShortDate(value) {
        if (!value) {
            return "";
        }
        const date = new Date(value);
        return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
    }

    function getFeedContainer() {
        return document.getElementById("feedContainer");
    }

    function feedPageEnabled() {
        return Boolean(document.getElementById("feedTabs") && getFeedContainer());
    }

    function showFeedSkeleton() {
        const container = getFeedContainer();
        if (!container) {
            return;
        }
        container.innerHTML = "<div class=\"skeleton-card\"></div><div class=\"skeleton-card\"></div><div class=\"skeleton-card\"></div>";
    }

    function registerExistingFeedIds() {
        const container = getFeedContainer();
        if (!container) {
            return;
        }

        container.querySelectorAll(".like-btn[data-id]").forEach((button) => {
            const rawId = button.dataset.id;
            if (!rawId) {
                return;
            }
            const parsed = Number(rawId);
            if (!Number.isNaN(parsed)) {
                state.loadedIds.add(parsed);
            }
        });
    }

    function createCardHTML(prompt) {
        const tagsHtml = (prompt.tags || [])
            .map((tag) => `<md-assist-chip label="${escapeHtml(tag)}" elevated></md-assist-chip>`)
            .join("");

        const difficulty = (prompt.difficulty || "").toLowerCase();
        const verifiedHtml = prompt.aiVerified
            ? `<span class="verified-badge"><md-icon style="font-size:16px;">verified</md-icon>Verified</span>`
            : "";
        const scoreHtml = prompt.aiScore
            ? `<span class="score-badge">AI Score ${escapeHtml(prompt.aiScore)}</span>`
            : "";

        const authorLetter = (prompt.authorUsername || "u").charAt(0).toUpperCase();

        return `
            <article class="prompt-card">
                <md-ripple></md-ripple>
                <div class="card-header">
                    <div class="author-info">
                        <a href="/users/${encodeURIComponent(prompt.authorUsername || "")}">
                            ${prompt.authorAvatar
                                ? `<img src="${escapeHtml(prompt.authorAvatar)}" class="avatar-sm" alt="Avatar">`
                                : `<div class="avatar-placeholder-sm">${escapeHtml(authorLetter)}</div>`}
                        </a>
                        <div class="author-details">
                            <a href="/users/${encodeURIComponent(prompt.authorUsername || "")}" class="author-name">${escapeHtml(prompt.authorName || "Unknown")}</a>
                            ${prompt.college ? `<span class="college-badge"><md-icon style="font-size:14px;">school</md-icon>${escapeHtml(prompt.college)}</span>` : ""}
                        </div>
                    </div>
                    <div class="card-meta">${formatShortDate(prompt.createdAt)}</div>
                </div>

                <a href="/prompts/${prompt.id}" class="card-title-link">
                    <h3 class="card-title">${escapeHtml(prompt.title)}</h3>
                </a>

                <div class="tags-container">
                    ${tagsHtml}
                    ${prompt.difficulty ? `<span class="difficulty-badge diff-${difficulty}">${escapeHtml(prompt.difficulty)}</span>` : ""}
                    ${prompt.platform ? `<span class="platform-badge">${escapeHtml(prompt.platform)}</span>` : ""}
                </div>

                ${(verifiedHtml || scoreHtml) ? `<div class="ai-status">${verifiedHtml}${scoreHtml}</div>` : ""}

                <div class="card-footer">
                    <div class="actions-left">
                        <md-icon-button class="like-btn ${prompt.liked ? "active" : ""}" data-id="${prompt.id}">
                            <md-icon ${prompt.liked ? 'style="font-variation-settings: \'FILL\' 1; color: var(--danger-color);"' : ""}>favorite</md-icon>
                        </md-icon-button>
                        <span class="like-count">${prompt.likeCount || 0}</span>
                        <md-icon-button onclick="window.location.href='/prompts/${prompt.id}#comments'">
                            <md-icon>chat_bubble_outline</md-icon>
                        </md-icon-button>
                        <md-icon-button class="copy-btn" data-id="${prompt.id}" data-copy="${escapeHtml(prompt.promptBody || "")}">
                            <md-icon>content_copy</md-icon>
                        </md-icon-button>
                    </div>
                    <div class="actions-right">
                        <md-icon-button class="save-btn ${prompt.saved ? "active" : ""}" data-id="${prompt.id}">
                            <md-icon ${prompt.saved ? 'style="font-variation-settings: \'FILL\' 1; color: var(--accent-color);"' : ""}>bookmark</md-icon>
                        </md-icon-button>
                    </div>
                </div>
            </article>
        `;
    }

    async function loadFeed(reset) {
        const container = getFeedContainer();
        if (!container || state.isLoading) {
            return;
        }

        state.isLoading = true;

        if (reset) {
            state.currentPage = 0;
            state.isLast = false;
            state.loadedIds.clear();
            showFeedSkeleton();
        }

        try {
            const response = await fetch(`/api/feed?tab=${encodeURIComponent(state.currentTab)}&page=${state.currentPage}`);
            if (!response.ok) {
                throw new Error("Failed to fetch feed");
            }

            const data = await response.json();
            const items = data.content || data.prompts || [];

            if (reset) {
                container.innerHTML = "";
            }

            if (items.length === 0 && state.currentPage === 0) {
                container.innerHTML = "<div class=\"empty-state\"><md-icon style=\"font-size:42px;\">edit_note</md-icon><p style=\"margin-top: 8px;\">No prompts available for this feed.</p></div>";
                state.isLast = true;
                return;
            }

            items.forEach((prompt) => {
                const parsedId = Number(prompt.id);
                if (!Number.isNaN(parsedId) && state.loadedIds.has(parsedId)) {
                    return;
                }

                if (!Number.isNaN(parsedId)) {
                    state.loadedIds.add(parsedId);
                }

                container.insertAdjacentHTML("beforeend", createCardHTML(prompt));
            });

            const totalPages = data.totalPages || 0;
            state.isLast = typeof data.last === "boolean"
                ? data.last
                : (totalPages > 0 ? state.currentPage >= totalPages - 1 : items.length === 0);

            if (!state.isLast) {
                state.currentPage += 1;
            }
        } catch (error) {
            container.innerHTML = "<div class=\"empty-state\"><md-icon style=\"font-size:42px;\">wifi_off</md-icon><p style=\"margin-top: 8px;\">Unable to load feed. Try refreshing.</p></div>";
        } finally {
            state.isLoading = false;
        }
    }

    function setupFeedTabs() {
        const tabs = document.getElementById("feedTabs");
        if (!tabs) {
            return;
        }

        tabs.addEventListener("change", () => {
            const selectedTab = tabs.tabs?.[tabs.activeTabIndex];
            const tab = selectedTab?.dataset.tab || "all";
            if (state.currentTab === tab && state.currentPage === 0) {
                return;
            }

            state.currentTab = tab;
            const nextUrl = new URL(window.location.href);
            if (tab === "all") {
                nextUrl.searchParams.delete("tab");
            } else {
                nextUrl.searchParams.set("tab", tab);
            }
            history.pushState({ tab }, "", nextUrl.toString());
            loadFeed(true);
        });
    }

    function setupInfiniteScroll() {
        window.addEventListener("scroll", () => {
            if (!feedPageEnabled()) {
                return;
            }
            if (state.isLoading || state.isLast) {
                return;
            }

            if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 220) {
                loadFeed(false);
            }
        });
    }

    function setIconState(button, active, colorVar) {
        const icon = button.querySelector("md-icon");
        if (!icon) {
            return;
        }

        if (active) {
            icon.style.fontVariationSettings = "'FILL' 1";
            icon.style.color = colorVar;
            button.classList.add("active");
        } else {
            icon.style.fontVariationSettings = "'FILL' 0";
            icon.style.color = "";
            button.classList.remove("active");
        }
    }

    function getLikeCountElement(button) {
        const sibling = button.nextElementSibling;
        if (sibling && sibling.classList.contains("like-count")) {
            return sibling;
        }

        const detailHeaderCount = button.parentElement?.querySelector(".like-count");
        if (detailHeaderCount) {
            return detailHeaderCount;
        }

        return null;
    }

    async function toggleLike(promptId, triggerButton) {
        const button = triggerButton || document.querySelector(`.like-btn[data-id="${promptId}"]`);
        if (!button || button.disabled) {
            return;
        }

        button.disabled = true;
        try {
            const response = await fetch(`/api/prompts/${promptId}/like`, { method: "POST" });
            if (response.status === 401) {
                window.location.href = "/login";
                return;
            }
            if (!response.ok) {
                throw new Error("Failed to toggle like");
            }

            const data = await response.json();
            setIconState(button, data.liked, "var(--danger-color)");

            const countElement = getLikeCountElement(button);
            if (countElement && typeof data.likeCount === "number") {
                countElement.textContent = String(data.likeCount);
            }
        } catch (error) {
            App.showToast("Failed to update like.", "error");
        } finally {
            button.disabled = false;
        }
    }

    async function toggleSave(promptId, triggerButton) {
        const button = triggerButton || document.querySelector(`.save-btn[data-id="${promptId}"]`);
        if (!button || button.disabled) {
            return;
        }

        button.disabled = true;
        try {
            const response = await fetch(`/api/prompts/${promptId}/save`, { method: "POST" });
            if (response.status === 401) {
                window.location.href = "/login";
                return;
            }
            if (!response.ok) {
                throw new Error("Failed to toggle save");
            }

            const data = await response.json();
            setIconState(button, data.saved, "var(--accent-color)");

            const saveCount = document.getElementById("saveCount");
            if (saveCount && typeof data.saveCount === "number") {
                saveCount.textContent = String(data.saveCount);
            }
        } catch (error) {
            App.showToast("Failed to update save.", "error");
        } finally {
            button.disabled = false;
        }
    }

    function appendComment(comment) {
        const list = document.getElementById("commentsList");
        if (!list) {
            return;
        }

        const item = document.createElement("article");
        item.className = "comment-item";

        const avatar = comment.avatarUrl
            ? `<img src="${escapeHtml(comment.avatarUrl)}" alt="Avatar" class="avatar-sm" style="width: 40px; height: 40px;">`
            : `<div class="avatar-placeholder-sm" style="width: 40px; height: 40px;">${escapeHtml((comment.authorName || "u").charAt(0).toUpperCase())}</div>`;

        const createdAt = comment.createdAt
            ? new Date(comment.createdAt).toLocaleString("en-US", { month: "short", day: "2-digit", hour: "2-digit", minute: "2-digit" })
            : "Now";

        item.innerHTML = `
            <div>${avatar}</div>
            <div class="comment-content">
                <div class="comment-header">
                    <span class="comment-author">${escapeHtml(comment.authorName || "User")}</span>
                    <span class="comment-time">${escapeHtml(createdAt)}</span>
                </div>
                <p>${escapeHtml(comment.body || "")}</p>
            </div>
        `;

        list.prepend(item);
    }

    async function addComment(promptId) {
        const input = document.getElementById("commentInput");
        if (!input) {
            return;
        }

        const text = (input.value || "").trim();
        if (!text) {
            App.showToast("Comment cannot be empty.", "error");
            return;
        }

        try {
            const response = await fetch(`/api/prompts/${promptId}/comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ text })
            });

            if (response.status === 401) {
                window.location.href = "/login";
                return;
            }
            if (!response.ok) {
                throw new Error("Failed to add comment");
            }

            const createdComment = await response.json();
            appendComment(createdComment);
            input.value = "";
            App.showToast("Comment posted.", "success");
        } catch (error) {
            App.showToast("Failed to post comment.", "error");
        }
    }

    function copyPrompt(text) {
        if (!text) {
            return;
        }

        navigator.clipboard.writeText(text)
            .then(() => App.showToast("Prompt copied.", "success"))
            .catch(() => App.showToast("Failed to copy prompt.", "error"));
    }

    function bindGlobalInteractions() {
        document.addEventListener("click", async (event) => {
            const likeBtn = event.target.closest("md-icon-button.like-btn, .like-btn");
            if (likeBtn) {
                await toggleLike(likeBtn.dataset.id, likeBtn);
                return;
            }

            const saveBtn = event.target.closest("md-icon-button.save-btn, .save-btn");
            if (saveBtn) {
                await toggleSave(saveBtn.dataset.id, saveBtn);
                return;
            }

            const copyBtn = event.target.closest("md-icon-button.copy-btn, .copy-btn");
            if (copyBtn) {
                const text = copyBtn.dataset.copy || document.getElementById("promptText")?.innerText || "";
                copyPrompt(text);
            }
        });
    }

    function initializeFeedPage() {
        if (!feedPageEnabled()) {
            return;
        }

        const params = new URLSearchParams(window.location.search);
        const tabFromUrl = params.get("tab");
        if (tabFromUrl) {
            state.currentTab = tabFromUrl;
            loadFeed(true);
        } else {
            registerExistingFeedIds();
        }

        setupFeedTabs();
        setupInfiniteScroll();

        window.onpopstate = () => {
            const urlParams = new URLSearchParams(window.location.search);
            state.currentTab = urlParams.get("tab") || "all";
            loadFeed(true);
        };
    }

    window.toggleLike = toggleLike;
    window.toggleSave = toggleSave;
    window.addComment = addComment;
    window.copyPrompt = copyPrompt;
    window.SurePromptCards = { createCardHTML };

    document.addEventListener("DOMContentLoaded", () => {
        bindGlobalInteractions();
        initializeFeedPage();
    });
})();
