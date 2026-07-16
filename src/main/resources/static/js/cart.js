document.addEventListener('DOMContentLoaded', () => {
    // Ajax Add to Cart triggers
    const addToCartButtons = document.querySelectorAll('.btn-add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const productId = button.getAttribute('data-product-id');
            addToCartAjax(productId, 1);
        });
    });
});

function addToCartAjax(productId, quantity) {
    const formData = new FormData();
    formData.append('productId', productId);
    formData.append('quantity', quantity);

    fetch('/cart/add', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        if (data.startsWith('SUCCESS:')) {
            const newCount = data.split(':')[1];
            
            // Update cart badges (if any)
            const badges = document.querySelectorAll('.nav-badge');
            badges.forEach(badge => {
                badge.textContent = newCount;
                badge.classList.remove('d-none');
            });

            // Show a beautiful toast notification
            showToast("Added to Cart", "Product successfully added to your order!", "success");
        } else {
            showToast("Stock Alert", data.replace('ERROR:', ''), "warning");
        }
    })
    .catch(error => {
        console.error('Error adding to cart:', error);
        showToast("Error", "Could not add item to cart. Please try again.", "danger");
    });
}

function showToast(title, message, type) {
    // Check if toast container exists
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.style.position = 'fixed';
        container.style.bottom = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0 show m-2`;
    toast.role = 'alert';
    toast.ariaLive = 'assertive';
    toast.ariaAtomic = 'true';
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <strong>${title}</strong>: ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    container.appendChild(toast);

    // Auto-remove after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 500);
    }, 3000);
}
