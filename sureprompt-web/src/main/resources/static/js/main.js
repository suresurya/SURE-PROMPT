// Global configuration and utilities
const App = {
    csrfToken: '',
    
    init: function() {
        // Find CSRF token if meta tags exist (Spring Security)
        // Since we disabled CSRF for /api, we might just pass basic headers
        
        // Setup global toast notifications
        this.setupToasts();
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
    
    showToast: function(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `alert-${type} toast-notification`;
        toast.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`;
        
        // Simple positioning
        toast.style.position = 'fixed';
        toast.style.bottom = '20px';
        toast.style.right = '20px';
        toast.style.zIndex = '9999';
        toast.style.padding = '15px 25px';
        toast.style.borderRadius = '8px';
        toast.style.boxShadow = '0 10px 30px rgba(0,0,0,0.5)';
        
        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.5s';
            setTimeout(() => toast.remove(), 500);
        }, 3000);
    },
    
    setupToasts: function() {
        // Auto hide server side alerts
        const alerts = document.querySelectorAll('.alert-success');
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
