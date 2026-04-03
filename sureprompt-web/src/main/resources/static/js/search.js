let searchTimeout = null;
let currentSearch = {
    q: '',
    difficulty: '',
    platform: '',
    tags: '',
    verifiedOnly: false,
    page: 0
};

document.addEventListener('DOMContentLoaded', () => {
    
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    const diffFilter = document.getElementById('diffFilter');
    const platformFilter = document.getElementById('platformFilter');
    const verifiedOnlyToggle = document.getElementById('verifiedOnlyToggle');
    const tagChips = document.querySelectorAll('.tag-chip');
    
    // Check URL parameters on load
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('q')) {
        searchInput.value = urlParams.get('q');
        currentSearch.q = urlParams.get('q');
        performSearch(true);
    }
    
    // Event listeners
    searchInput.addEventListener('input', (e) => {
        currentSearch.q = e.target.value;
        debounceSearch();
    });

    diffFilter.addEventListener('change', (e) => {
        currentSearch.difficulty = e.target.value;
        performSearch(true);
    });

    platformFilter.addEventListener('change', (e) => {
        currentSearch.platform = e.target.value;
        performSearch(true);
    });

    verifiedOnlyToggle.addEventListener('change', (e) => {
        currentSearch.verifiedOnly = e.target.checked;
        performSearch(true);
    });

    tagChips.forEach(chip => {
        chip.addEventListener('click', (e) => {
            const tag = e.target.dataset.tag;
            
            // Toggle active state
            if (tag === '') {
                // "All Topics" clicked
                tagChips.forEach(c => c.classList.remove('active'));
                e.target.classList.add('active');
                currentSearch.tags = '';
            } else {
                // Specific tag clicked
                document.querySelector('.tag-chip[data-tag=""]').classList.remove('active');
                e.target.classList.toggle('active');
                
                // Collect active tags
                const activeTags = [];
                document.querySelectorAll('.tag-chip.active').forEach(c => {
                    if (c.dataset.tag !== '') activeTags.push(c.dataset.tag);
                });
                
                if (activeTags.length === 0) {
                    document.querySelector('.tag-chip[data-tag=""]').classList.add('active');
                }
                
                currentSearch.tags = activeTags.join(',');
            }
            
            performSearch(true);
        });
    });
});

function debounceSearch() {
    if (searchTimeout) clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        performSearch(true);
    }, 500); // 500ms delay while typing
}

async function performSearch(resetPage = false) {
    if (resetPage) currentSearch.page = 0;
    
    const resultsContainer = document.getElementById('searchResults');
    resultsContainer.innerHTML = '<div class="text-center w-100 py-5"><i class="fas fa-spinner fa-spin"></i> Searching...</div>';
    
    try {
        let url = `/api/search?page=${currentSearch.page}`;
        if (currentSearch.q) url += `&q=${encodeURIComponent(currentSearch.q)}`;
        if (currentSearch.tags) url += `&tags=${encodeURIComponent(currentSearch.tags)}`;
        if (currentSearch.difficulty) url += `&difficulty=${encodeURIComponent(currentSearch.difficulty)}`;
        if (currentSearch.platform) url += `&platform=${encodeURIComponent(currentSearch.platform)}`;
        if (currentSearch.verifiedOnly) url += `&verifiedOnly=${currentSearch.verifiedOnly}`;
        
        const data = await App.fetchAuth(url);
        
        // Update URL to make it shareable without reloading
        const stateUrl = `?q=${encodeURIComponent(currentSearch.q)}`;
        window.history.replaceState(null, '', stateUrl);
        
        resultsContainer.innerHTML = '';
        
        const metaContainer = document.getElementById('resultsMeta');
        metaContainer.style.display = 'block';
        document.getElementById('totalResults').innerText = data.totalResults;
        document.getElementById('appliedFilters').innerText = data.appliedFilters ? `(Filters: ${data.appliedFilters})` : '';
        
        if (data.results.length === 0) {
            resultsContainer.innerHTML = '<div class="text-center w-100 py-5 text-muted">No prompts found matching your criteria.</div>';
            return;
        }

        data.results.forEach(p => {
            // Note: createCardHTML is defined in feed.js. Make sure feed.js is included before search.js
            if(typeof createCardHTML === 'function') {
                resultsContainer.insertAdjacentHTML('beforeend', createCardHTML(p));
            }
        });
        
    } catch (error) {
        resultsContainer.innerHTML = '<div class="text-center w-100 py-5 text-danger">Search failed.</div>';
    }
}
