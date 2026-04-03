// Feed infinite scroll and tab logic
let currentTab = 'all';
let currentPage = 0;
let isLoading = false;
let hasMore = true;

document.addEventListener('DOMContentLoaded', () => {
    
    // Only run if on feed page
    const feedContainer = document.getElementById('feed-container');
    if (!feedContainer) return;

    loadFeed(true);

    // Tab switching
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
            
            currentTab = e.target.dataset.tab;
            loadFeed(true);
        });
    });

    // Infinite scroll
    window.addEventListener('scroll', () => {
        if (isLoading || !hasMore) return;
        
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
            loadFeed(false);
        }
    });
});

async function loadFeed(reset) {
    if (reset) {
        currentPage = 0;
        hasMore = true;
        document.getElementById('feed-container').innerHTML = '<div class="loading-spinner"><i class="fas fa-spinner fa-spin"></i> Loading...</div>';
    }
    
    isLoading = true;
    
    try {
        const data = await App.fetchAuth(`/api/feed?tab=${currentTab}&page=${currentPage}`);
        
        if (reset) {
            document.getElementById('feed-container').innerHTML = '';
        }
        
        if (data.prompts.length === 0) {
            if (reset) {
                document.getElementById('feed-container').innerHTML = '<div class="text-center py-5 text-muted">No prompts found.</div>';
            }
            hasMore = false;
            return;
        }

        const container = document.getElementById('feed-container');
        data.prompts.forEach(p => {
            container.insertAdjacentHTML('beforeend', createCardHTML(p));
        });

        currentPage++;
        hasMore = currentPage < data.totalPages;
        
    } finally {
        isLoading = false;
    }
}

function createCardHTML(p) {
    const tagsHtml = p.tags.map(t => `<span class="tag">${t}</span>`).join('');
    
    let aiHtml = '';
    if (p.aiVerified || p.aiScore) {
        aiHtml = `<div class="ai-status">`;
        if (p.aiVerified) aiHtml += `<div class="verified-badge"><i class="fas fa-check-circle"></i> AI Verified</div>`;
        if (p.aiScore) aiHtml += `<div class="score-badge">AI Score: ${p.aiScore}/10</div>`;
        aiHtml += `</div>`;
    }

    // Format date roughly
    const date = new Date(p.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

    return `
        <div class="prompt-card">
            <div class="card-header">
                <div class="author-info">
                    <a href="/users/${p.authorUsername}">
                        ${p.authorAvatar ? `<img src="${p.authorAvatar}" class="avatar-sm">` : `<div class="avatar-placeholder-sm">${p.authorUsername.charAt(0).toUpperCase()}</div>`}
                    </a>
                    <div class="author-details">
                        <a href="/users/${p.authorUsername}" class="author-name">${p.authorName}</a>
                        ${p.college ? `<span class="college-badge"><i class="fas fa-university"></i> ${p.college}</span>` : ''}
                    </div>
                </div>
                <div class="card-meta">
                    <span class="time-ago">${date}</span>
                </div>
            </div>
            
            <div class="card-body">
                <a href="/prompts/${p.id}" class="card-title-link">
                    <h3 class="card-title">${p.title}</h3>
                </a>
                
                <div class="tags-container">
                    ${tagsHtml}
                    <span class="difficulty-badge diff-${p.difficulty.toLowerCase()}">${p.difficulty}</span>
                    <span class="platform-badge">${p.platform}</span>
                </div>
                
                ${aiHtml}
            </div>
            
            <div class="card-footer">
                <div class="actions-left">
                    <button class="action-btn like-btn ${p.liked ? 'active' : ''}" data-id="${p.id}">
                        <i class="${p.liked ? 'fas fa-heart' : 'far fa-heart'}"></i>
                        <span class="count">${p.likeCount}</span>
                    </button>
                    <button class="action-btn comment-btn" onclick="window.location.href='/prompts/${p.id}#comments'">
                        <i class="far fa-comment"></i>
                    </button>
                    <button class="action-btn copy-btn" data-id="${p.id}" title="View Prompt">
                        <i class="far fa-eye"></i>
                    </button>
                </div>
                <div class="actions-right">
                    <button class="action-btn save-btn ${p.saved ? 'active' : ''}" data-id="${p.id}">
                        <i class="${p.saved ? 'fas fa-bookmark' : 'far fa-bookmark'}"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
}
