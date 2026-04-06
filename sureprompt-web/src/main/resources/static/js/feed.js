// =========================================================================
// Feed — Infinite scroll and tab logic (MD3 edition)
// =========================================================================

let currentTab = 'all';
let currentPage = 0;
let isLoading = false;
let hasMore = true;

document.addEventListener('DOMContentLoaded', () => {
    
    const feedContainer = document.getElementById('feed-container');
    if (!feedContainer) return;

    loadFeed(true);

    // Tab switching via MD3 tabs
    const mdTabs = document.getElementById('feedTabs');
    if (mdTabs) {
        mdTabs.addEventListener('change', () => {
            const selectedTab = mdTabs.tabs[mdTabs.activeTabIndex];
            if (selectedTab) {
                currentTab = selectedTab.dataset.tab || 'all';
                loadFeed(true);
            }
        });
    }

    // Legacy tab button switching
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
        const container = document.getElementById('feed-container');
        if (container) {
            container.innerHTML = `
                <div style="text-align: center; padding: 40px; color: var(--text-muted);">
                    <md-circular-progress indeterminate style="--md-circular-progress-size: 40px;"></md-circular-progress>
                    <p style="margin-top: 16px;">Loading...</p>
                </div>`;
        }
    }
    
    isLoading = true;
    
    try {
        const data = await App.fetchAuth(`/api/feed?tab=${currentTab}&page=${currentPage}`);
        
        if (reset) {
            document.getElementById('feed-container').innerHTML = '';
        }
        
        if (data.prompts.length === 0) {
            if (reset) {
                document.getElementById('feed-container').innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: var(--text-muted);">
                        <md-icon style="font-size: 3rem; display: block; margin-bottom: 12px; opacity: 0.4;">edit_note</md-icon>
                        No prompts found.
                    </div>`;
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
    const tagsHtml = (p.tags || []).map(t => `<md-assist-chip label="${t}" elevated></md-assist-chip>`).join('');
    
    let aiHtml = '';
    if (p.aiVerified || p.aiScore) {
        aiHtml = `<div class="ai-status">`;
        if (p.aiVerified) aiHtml += `<div class="verified-badge"><md-icon style="font-size:16px;color:var(--success-color);">verified</md-icon> AI Verified</div>`;
        if (p.aiScore) aiHtml += `<div class="score-badge">AI Score: ${p.aiScore}/10</div>`;
        aiHtml += `</div>`;
    }

    const date = new Date(p.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

    return `
        <div class="prompt-card">
            <md-ripple></md-ripple>
            <div class="card-header">
                <div class="author-info">
                    <a href="/users/${p.authorUsername}">
                        ${p.authorAvatar ? `<img src="${p.authorAvatar}" class="avatar-sm">` : `<div class="avatar-placeholder-sm">${p.authorUsername.charAt(0).toUpperCase()}</div>`}
                    </a>
                    <div class="author-details">
                        <a href="/users/${p.authorUsername}" class="author-name">${p.authorName}</a>
                        ${p.college ? `<span class="college-badge"><md-icon style="font-size:14px;">school</md-icon> ${p.college}</span>` : ''}
                    </div>
                </div>
                <div class="card-meta">
                    <span style="color: var(--text-muted); font-size: 0.85rem;">${date}</span>
                </div>
            </div>
            
            <div class="card-body">
                <a href="/prompts/${p.id}" class="card-title-link">
                    <h3 class="card-title">${p.title}</h3>
                </a>
                
                <div class="tags-container">
                    ${tagsHtml}
                    <span class="difficulty-badge diff-${(p.difficulty||'').toLowerCase()}">${p.difficulty || ''}</span>
                    <span class="platform-badge">${p.platform || ''}</span>
                </div>
                
                ${aiHtml}
            </div>
            
            <md-divider style="margin: 0 -24px; width: calc(100% + 48px);"></md-divider>
            
            <div class="card-footer" style="padding-top: 12px;">
                <div class="actions-left">
                    <md-icon-button class="like-btn ${p.liked ? 'active' : ''}" data-id="${p.id}">
                        <md-icon${p.liked ? ' style="font-variation-settings: \'FILL\' 1; color: var(--danger-color);"' : ''}>favorite</md-icon>
                    </md-icon-button>
                    <span class="count" style="font-size:0.85rem; color:var(--text-muted);">${p.likeCount}</span>
                    <md-icon-button onclick="window.location.href='/prompts/${p.id}#comments'">
                        <md-icon>chat_bubble_outline</md-icon>
                    </md-icon-button>
                </div>
                <div class="actions-right">
                    <md-icon-button class="save-btn ${p.saved ? 'active' : ''}" data-id="${p.id}">
                        <md-icon${p.saved ? ' style="font-variation-settings: \'FILL\' 1; color: var(--accent-color);"' : ''}>bookmark</md-icon>
                    </md-icon-button>
                </div>
            </div>
        </div>
    `;
}
