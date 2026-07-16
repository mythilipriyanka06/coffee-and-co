document.addEventListener('DOMContentLoaded', () => {
    console.log("Coffee & Co Theme Loaded!");

    // Smooth page transitions or animation load triggers
    const animateElements = document.querySelectorAll('.animate-fade-in');
    animateElements.forEach((el, index) => {
        setTimeout(() => {
            el.style.opacity = '1';
            el.style.transform = 'translateY(0)';
        }, index * 100);
    });

    // Client-side live search for product cards
    const liveSearchInput = document.getElementById('liveSearchInput');
    if (liveSearchInput) {
        liveSearchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase().trim();
            const productCards = document.querySelectorAll('.product-card-wrapper');
            let foundCount = 0;

            productCards.forEach(card => {
                const productName = card.querySelector('.card-title').textContent.toLowerCase();
                const productDesc = card.querySelector('.card-text').textContent.toLowerCase();
                
                if (productName.includes(query) || productDesc.includes(query)) {
                    card.style.display = 'block';
                    foundCount++;
                } else {
                    card.style.display = 'none';
                }
            });

            // Display "No products found" if count is 0
            const noProductsAlert = document.getElementById('noProductsAlert');
            if (noProductsAlert) {
                noProductsAlert.style.display = foundCount === 0 ? 'block' : 'none';
            }
        });
    }

    // Modal populate helpers for admin editing
    setupCategoryEditModals();
    setupProductEditModals();
    setupUserEditModals();
});

// Admin Category Edit Modal Populator
function setupCategoryEditModals() {
    const editCatButtons = document.querySelectorAll('.btn-edit-category');
    editCatButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const name = btn.getAttribute('data-name');
            const desc = btn.getAttribute('data-desc');

            const modalId = document.getElementById('editCategoryId');
            const modalName = document.getElementById('editCategoryName');
            const modalDesc = document.getElementById('editCategoryDescription');

            if (modalId && modalName && modalDesc) {
                modalId.value = id;
                modalName.value = name;
                modalDesc.value = desc;
            }
        });
    });
}

// Admin Product Edit Modal Populator
function setupProductEditModals() {
    const editProdButtons = document.querySelectorAll('.btn-edit-product');
    editProdButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const name = btn.getAttribute('data-name');
            const desc = btn.getAttribute('data-desc');
            const price = btn.getAttribute('data-price');
            const stock = btn.getAttribute('data-stock');
            const available = btn.getAttribute('data-available') === 'true';
            const catId = btn.getAttribute('data-category-id');
            const imgPath = btn.getAttribute('data-image-path');

            const modalId = document.getElementById('editProductId');
            const modalName = document.getElementById('editProductName');
            const modalDesc = document.getElementById('editProductDescription');
            const modalPrice = document.getElementById('editProductPrice');
            const modalStock = document.getElementById('editProductStock');
            const modalAvailable = document.getElementById('editProductAvailable');
            const modalCat = document.getElementById('editProductCategory');
            const modalImg = document.getElementById('editProductImagePath');

            if (modalId && modalName && modalDesc && modalPrice && modalStock && modalAvailable && modalCat && modalImg) {
                modalId.value = id;
                modalName.value = name;
                modalDesc.value = desc;
                modalPrice.value = price;
                modalStock.value = stock;
                modalAvailable.value = available ? 'true' : 'false';
                modalCat.value = catId;
                modalImg.value = imgPath;
            }
        });
    });
}

// Admin User Edit Modal Populator
function setupUserEditModals() {
    const editUserButtons = document.querySelectorAll('.btn-edit-user');
    editUserButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const role = btn.getAttribute('data-role');

            const modalId = document.getElementById('editUserId');
            const modalRole = document.getElementById('editUserRole');

            if (modalId && modalRole) {
                modalId.value = id;
                modalRole.value = role;
            }
        });
    });
}
