// Logic for interactions on prompt cards and prompt detail page
document.addEventListener('DOMContentLoaded', () => {

    // Toggle Like
    document.body.addEventListener('click', async (e) => {
        const likeBtn = e.target.closest('.like-btn');
        if (likeBtn) {
            e.preventDefault();
            const id = likeBtn.dataset.id;
            const res = await App.fetchAuth(`/api/prompts/${id}/like`, { method: 'POST' });
            if (res) {
                likeBtn.classList.toggle('active', res.liked);
                likeBtn.querySelector('i').className = res.liked ? 'fas fa-heart' : 'far fa-heart';
                likeBtn.querySelector('.count').innerText = res.likeCount;
            }
        }
    });

    // Toggle Save
    document.body.addEventListener('click', async (e) => {
        const saveBtn = e.target.closest('.save-btn');
        if (saveBtn) {
            e.preventDefault();
            const id = saveBtn.dataset.id;
            const res = await App.fetchAuth(`/api/prompts/${id}/save`, { method: 'POST' });
            if (res) {
                saveBtn.classList.toggle('active', res.saved);
                saveBtn.querySelector('i').className = res.saved ? 'fas fa-bookmark' : 'far fa-bookmark';
            }
        }
    });

    // Copy Prompt
    document.body.addEventListener('click', (e) => {
        const copyBtn = e.target.closest('.copy-btn');
        if (copyBtn) {
            e.preventDefault();
            
            // If on detail page, grab from element
            const promptTextEl = document.getElementById('promptText');
            if (promptTextEl) {
                navigator.clipboard.writeText(promptTextEl.innerText).then(() => {
                    App.showToast('Prompt copied to clipboard!');
                    const icon = copyBtn.querySelector('i');
                    icon.className = 'fas fa-check';
                    setTimeout(() => icon.className = 'far fa-copy', 2000);
                });
            } else {
                // In feed, we might need to fetch it or store it in data attrib.
                // Simplified for now: redirect to detail
                window.location.href = `/prompts/${copyBtn.dataset.id}`;
            }
        }
    });
    
    // Add Comment
    const postCommentBtn = document.getElementById('postCommentBtn');
    if (postCommentBtn) {
        postCommentBtn.addEventListener('click', async () => {
            const input = document.getElementById('commentInput');
            const body = input.value.trim();
            const id = input.dataset.id;
            
            if (!body) return;
            
            const res = await App.fetchAuth(`/api/prompts/${id}/comments`, {
                method: 'POST',
                body: JSON.stringify({ body })
            });
            
            if (res) {
                input.value = '';
                
                // Construct new comment HTML
                const commentList = document.getElementById('commentsList');
                const noComments = document.querySelector('.no-comments');
                if(noComments) noComments.style.display = 'none';
                
                const html = `
                    <div class="comment-item" id="comment-${res.id}">
                        ${res.avatarUrl ? `<img src="${res.avatarUrl}" class="avatar-sm">` : `<div class="avatar-placeholder-sm">${res.authorUsername.substring(0,1).toUpperCase()}</div>`}
                        <div class="comment-content">
                            <div class="comment-header">
                                <a href="/users/${res.authorUsername}" class="comment-author">${res.authorName}</a>
                                <span class="comment-time">Just now</span>
                                <button class="btn-icon delete-comment-btn" data-comment-id="${res.id}" data-prompt-id="${id}"><i class="fas fa-trash"></i></button>
                            </div>
                            <div class="comment-text">${res.body}</div>
                        </div>
                    </div>
                `;
                commentList.insertAdjacentHTML('afterbegin', html);
                App.showToast('Comment posted');
            }
        });
    }

    // Delete Comment
    document.body.addEventListener('click', async (e) => {
        const deleteBtn = e.target.closest('.delete-comment-btn');
        if (deleteBtn) {
            e.preventDefault();
            if(!confirm("Delete this comment?")) return;
            
            const commentId = deleteBtn.dataset.commentId;
            const promptId = deleteBtn.dataset.promptId;
            
            const res = await App.fetchAuth(`/api/prompts/${promptId}/comments/${commentId}`, { method: 'DELETE' });
            if (res !== null) {
                document.getElementById(`comment-${commentId}`).remove();
                App.showToast('Comment deleted');
            }
        }
    });
    
    // Delete Prompt
    const deletePromptBtn = document.querySelector('.pd-delete-btn');
    if (deletePromptBtn) {
        deletePromptBtn.addEventListener('click', () => {
            if(confirm("Are you sure you want to delete this prompt? This cannot be undone.")) {
                document.getElementById('delete-form-' + deletePromptBtn.dataset.id).submit();
            }
        });
    }
});
