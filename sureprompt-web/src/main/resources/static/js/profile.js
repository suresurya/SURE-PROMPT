// =========================================================================
// Profile Page — Client-side interactivity
// =========================================================================

document.addEventListener("DOMContentLoaded", () => {
    initFollowButton();
    initLikeSaveButtons();
    initFollowButtonHover();
});

// ==================== FOLLOW BUTTON ====================

function initFollowButton() {
    document.addEventListener("click", async (e) => {
        const btn = e.target.closest(".follow-btn");
        if (!btn) return;

        const userId = btn.dataset.userid;
        if (!userId) return;

        btn.disabled = true;

        try {
            const res = await fetch(`/api/users/${userId}/follow`, { method: "POST" });

            if (res.status === 401) {
                window.location.href = "/login";
                return;
            }

            if (!res.ok) throw new Error("Failed");

            const data = await res.json();

            // Update button state
            if (data.following) {
                btn.textContent = "Following";
                btn.classList.add("following");
                btn.classList.remove("btn-primary");
            } else {
                btn.textContent = "Follow";
                btn.classList.remove("following");
                btn.classList.add("btn-primary");
            }

            // Update followers count in stats
            const countEl = document.getElementById("followersCount");
            if (countEl && data.followersCount !== undefined) {
                countEl.textContent = data.followersCount;
            }
        } catch (err) {
            console.error("Follow toggle failed:", err);
        } finally {
            btn.disabled = false;
        }
    });
}

// Follow button hover state: "Following" → "Unfollow"
function initFollowButtonHover() {
    document.addEventListener("mouseenter", (e) => {
        const btn = e.target.closest(".follow-btn.following");
        if (btn) btn.textContent = "Unfollow";
    }, true);

    document.addEventListener("mouseleave", (e) => {
        const btn = e.target.closest(".follow-btn.following");
        if (btn) btn.textContent = "Following";
    }, true);
}

// ==================== LIKE / SAVE BUTTONS ====================

function initLikeSaveButtons() {
    document.addEventListener("click", async (e) => {
        // Like
        const likeBtn = e.target.closest(".like-btn");
        if (likeBtn) {
            const promptId = likeBtn.dataset.id;
            try {
                const res = await fetch(`/api/prompts/${promptId}/like`, { method: "POST" });
                if (res.status === 401) { window.location.href = "/login"; return; }
                if (!res.ok) throw new Error("Failed");

                const data = await res.json();
                const icon = likeBtn.querySelector("i");
                const count = likeBtn.querySelector(".like-count");

                if (data.liked) {
                    likeBtn.classList.add("active");
                    icon.className = "fas fa-heart";
                } else {
                    likeBtn.classList.remove("active");
                    icon.className = "far fa-heart";
                }
                if (count) count.textContent = data.likeCount;
            } catch (err) {
                console.error("Like failed:", err);
            }
            return;
        }

        // Save
        const saveBtn = e.target.closest(".save-btn");
        if (saveBtn) {
            const promptId = saveBtn.dataset.id;
            try {
                const res = await fetch(`/api/prompts/${promptId}/save`, { method: "POST" });
                if (res.status === 401) { window.location.href = "/login"; return; }
                if (!res.ok) throw new Error("Failed");

                const data = await res.json();
                const icon = saveBtn.querySelector("i");

                if (data.saved) {
                    saveBtn.classList.add("active");
                    icon.className = "fas fa-bookmark";
                } else {
                    saveBtn.classList.remove("active");
                    icon.className = "far fa-bookmark";
                }
            } catch (err) {
                console.error("Save failed:", err);
            }
            return;
        }
    });
}
