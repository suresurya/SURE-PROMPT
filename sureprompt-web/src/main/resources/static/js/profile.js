// =========================================================================
// Profile Page — Client-side interactivity (MD3 edition)
// =========================================================================

document.addEventListener("DOMContentLoaded", () => {
    initFollowButton();
    initLikeSaveButtons();
    initFollowButtonHover();
});

// ==================== FOLLOW BUTTON ====================

function initFollowButton() {
    document.addEventListener("click", async (e) => {
        const btn = e.target.closest(".follow-btn, md-filled-button.follow-btn, md-outlined-button.follow-btn");
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

            // Swap button appearance
            if (data.following) {
                btn.textContent = "";
                btn.innerHTML = '<md-icon slot="icon">check</md-icon> Following';
                btn.classList.add("following");
                btn.classList.remove("btn-primary");
            } else {
                btn.textContent = "";
                btn.innerHTML = '<md-icon slot="icon">person_add</md-icon> Follow';
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
            App.showToast("Failed to update follow status", "error");
        } finally {
            btn.disabled = false;
        }
    });
}

// Follow button hover state: "Following" → "Unfollow"
function initFollowButtonHover() {
    document.addEventListener("mouseenter", (e) => {
        const btn = e.target.closest(".follow-btn.following");
        if (btn) {
            btn.innerHTML = '<md-icon slot="icon">person_remove</md-icon> Unfollow';
        }
    }, true);

    document.addEventListener("mouseleave", (e) => {
        const btn = e.target.closest(".follow-btn.following");
        if (btn) {
            btn.innerHTML = '<md-icon slot="icon">check</md-icon> Following';
        }
    }, true);
}

// ==================== LIKE / SAVE BUTTONS ====================

function initLikeSaveButtons() {
    document.addEventListener("click", async (e) => {
        // Like
        const likeBtn = e.target.closest("md-icon-button.like-btn, .like-btn");
        if (likeBtn) {
            const promptId = likeBtn.dataset.id;
            if (!promptId) return;
            try {
                const res = await fetch(`/api/prompts/${promptId}/like`, { method: "POST" });
                if (res.status === 401) { window.location.href = "/login"; return; }
                if (!res.ok) throw new Error("Failed");

                const data = await res.json();
                const icon = likeBtn.querySelector("md-icon");

                if (data.liked) {
                    likeBtn.classList.add("active");
                    if (icon) {
                        icon.style.fontVariationSettings = "'FILL' 1";
                        icon.style.color = "var(--danger-color)";
                    }
                } else {
                    likeBtn.classList.remove("active");
                    if (icon) {
                        icon.style.fontVariationSettings = "'FILL' 0";
                        icon.style.color = "";
                    }
                }
                
                const count = likeBtn.parentElement?.querySelector(".like-count");
                if (count) count.textContent = data.likeCount;
            } catch (err) {
                console.error("Like failed:", err);
            }
            return;
        }

        // Save
        const saveBtn = e.target.closest("md-icon-button.save-btn, .save-btn");
        if (saveBtn) {
            const promptId = saveBtn.dataset.id;
            if (!promptId) return;
            try {
                const res = await fetch(`/api/prompts/${promptId}/save`, { method: "POST" });
                if (res.status === 401) { window.location.href = "/login"; return; }
                if (!res.ok) throw new Error("Failed");

                const data = await res.json();
                const icon = saveBtn.querySelector("md-icon");

                if (data.saved) {
                    saveBtn.classList.add("active");
                    if (icon) {
                        icon.style.fontVariationSettings = "'FILL' 1";
                        icon.style.color = "var(--accent-color)";
                    }
                } else {
                    saveBtn.classList.remove("active");
                    if (icon) {
                        icon.style.fontVariationSettings = "'FILL' 0";
                        icon.style.color = "";
                    }
                }
            } catch (err) {
                console.error("Save failed:", err);
            }
            return;
        }
    });
}
