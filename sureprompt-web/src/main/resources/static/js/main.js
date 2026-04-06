// =========================================================================
// SurePrompt Global JS — Material Design 3 Edition
// =========================================================================

const App = {
    csrfToken: '',
    
    init: function() {
        this.setupToasts();
        this.setupAutoHideAlerts();
    },
    
    fetchAuth: async function(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };
        const finalOptions = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(url, finalOptions);
            
            if (response.status === 401) {
                window.location.href = '/login';
                return null;
            }
            
            if (!response.ok) {
                throw new Error("API Request Failed");
            }
            
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('Fetch error:', error);
            App.showToast('Something went wrong', 'error');
            throw error;
        }
    },
    
    // Material Design 3 Snackbar Toast
    showToast: function(message, type = 'success') {
        // Remove existing snackbar
        const existing = document.querySelector('.md3-snackbar');
        if (existing) existing.remove();

        const iconMap = {
            success: 'check_circle',
            error: 'error',
            info: 'info'
        };

        const snackbar = document.createElement('div');
        snackbar.className = `md3-snackbar ${type}`;
        snackbar.innerHTML = `
            <md-icon>${iconMap[type] || 'info'}</md-icon>
            <span>${message}</span>
        `;
        
        document.body.appendChild(snackbar);

        // Trigger animation
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                snackbar.classList.add('show');
            });
        });
        
        setTimeout(() => {
            snackbar.classList.remove('show');
            setTimeout(() => snackbar.remove(), 300);
        }, 4000);
    },
    
    setupToasts: function() {
        // No-op, kept for backward compat
    },

    setupAutoHideAlerts: function() {
        const alerts = document.querySelectorAll('.alert-success, #successAlert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.opacity = '0';
                alert.style.transition = 'opacity 0.5s';
                setTimeout(() => alert.style.display = 'none', 500);
            }, 4000);
        });
    }
};

document.addEventListener('DOMContentLoaded', () => App.init());
