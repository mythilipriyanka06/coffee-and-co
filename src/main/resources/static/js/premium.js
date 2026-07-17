/* =========================================================
   COFFEE & CO — PREMIUM FEATURES (JS)
   Cleaned up floating buttons, interactive HTML5 canvas scratch card,
   Today's Special, Combo Deals, and other premium widgets.
   ========================================================= */

/* ------------------------------------------------------------------
   1️⃣ Quote of the Day – random coffee quote with fade animation
   ------------------------------------------------------------------ */
const quotes = [
  {text: "Behind every successful person is a substantial amount of coffee.", author: "Steven Redhead"},
  {text: "I orchestrate my day with coffee as the conductor.", author: "Unknown"},
  {text: "Coffee is a language in itself.", author: "Jackie Chan"},
  {text: "Life is too short for bad coffee.", author: "Anonymous"},
  {text: "Coffee – because adulting is hard.", author: "Unknown"}
];
function showQuote(){
  const container = document.createElement('div');
  container.className = 'quote-strip slide-up-on-scroll';
  const random = quotes[Math.floor(Math.random()*quotes.length)];
  container.innerHTML = `<i class="fa-solid fa-mug-hot quote-icon"></i>"${random.text}"<div class="quote-author">- ${random.author}</div>`;
  // Insert after navbar
  const navbar = document.querySelector('header');
  if(navbar){ navbar.parentNode.insertBefore(container, navbar.nextSibling); }
  // Fade in using CSS transition
  setTimeout(()=>{container.style.opacity = '1';}, 100);
}

/* ------------------------------------------------------------------
   2️⃣ Offers Banner – auto‑sliding promotional offers
   ------------------------------------------------------------------ */
function initOffersBanner(){
  const offers = [
    {icon: 'fa-gift', text: 'Buy 1 Get 1 Free'},
    {icon: 'fa-percent', text: 'Flat 20% Off Every Friday'},
    {icon: 'fa-birthday-cake', text: 'Free Brownie on Orders Above ₹799'},
    {icon: 'fa-user-graduate', text: 'Student Special 15% Off'},
    {icon: 'fa-clock', text: 'Happy Hours 4 PM – 6 PM'}
  ];
  const banner = document.createElement('div');
  banner.className = 'offers-banner';
  const track = document.createElement('div');
  track.className = 'offers-track';
  offers.forEach(o=>{
    const item = document.createElement('div');
    item.className = 'offer-item';
    item.innerHTML = `<i class="fa-solid ${o.icon} offer-icon"></i><span class="offer-highlight">${o.text}</span>`;
    track.appendChild(item);
  });
  // duplicate for seamless loop
  offers.forEach(o=>{
    const item = document.createElement('div');
    item.className = 'offer-item';
    item.innerHTML = `<i class="fa-solid ${o.icon} offer-icon"></i><span class="offer-highlight">${o.text}</span>`;
    track.appendChild(item);
  });
  banner.appendChild(track);
  const navbar = document.querySelector('header');
  if(navbar){ navbar.parentNode.insertBefore(banner, navbar); }
}

/* ------------------------------------------------------------------
   3️⃣ Dark/Light Mode Toggle – persists in localStorage
   ------------------------------------------------------------------ */
function initDarkMode(){
  const toggle = document.createElement('button');
  toggle.className = 'dark-mode-toggle';
  toggle.innerHTML = '<i class="fa-solid fa-moon"></i>';
  const navbar = document.querySelector('.navbar-coffee');
  if(navbar){ navbar.appendChild(toggle); }
  const applyTheme = (theme)=>{
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
    toggle.innerHTML = theme === 'dark' ? '<i class="fa-solid fa-sun"></i>' : '<i class="fa-solid fa-moon"></i>';
  };
  const saved = localStorage.getItem('theme') || 'light';
  applyTheme(saved);
  toggle.addEventListener('click',()=>{ applyTheme(document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark'); });
}

/* ------------------------------------------------------------------
   4️⃣ Best Seller / Trending / Customer Favorites Badges
   ------------------------------------------------------------------ */
function markSpecialProducts(){
  const cards = document.querySelectorAll('.product-card-wrapper');
  const sorted = Array.from(cards).sort((a,b)=>{
    const priceA = parseFloat(a.querySelector('.product-price').textContent.replace(/[₹,]/g,''));
    const priceB = parseFloat(b.querySelector('.product-price').textContent.replace(/[₹,]/g,''));
    return priceB - priceA; // descending
  });
  const bestSellers = sorted.slice(0,4);
  const trending = sorted.slice(4,8);
  const favorites = sorted.slice(8,12);
  bestSellers.forEach(c=>{ addBadge(c,'badge-best-seller','Best Seller'); });
  trending.forEach(c=>{ addBadge(c,'badge-trending','Trending'); });
  favorites.forEach(c=>{ addBadge(c,'badge-favorite','Favorite'); });
}
function addBadge(card, className, text){
  const badge = document.createElement('div');
  badge.className = className;
  badge.textContent = text;
  card.style.position = 'relative';
  card.appendChild(badge);
}

/* ------------------------------------------------------------------
   5️⃣ Wishlist & Quick View overlay (added to each product card)
   ------------------------------------------------------------------ */
function initProductOverlays(){
  const cards = document.querySelectorAll('.product-card-wrapper');
  cards.forEach(card=>{
    const overlay = document.createElement('div');
    overlay.className = 'product-overlay-actions';
    const wishBtn = document.createElement('button');
    wishBtn.className = 'overlay-btn';
    wishBtn.innerHTML = '<i class="fa-solid fa-heart"></i>';
    const viewBtn = document.createElement('button');
    viewBtn.className = 'overlay-btn';
    viewBtn.innerHTML = '<i class="fa-solid fa-eye"></i>';
    overlay.appendChild(wishBtn);
    overlay.appendChild(viewBtn);
    card.appendChild(overlay);
    
    // Wishlist handling
    const btnAddToCart = card.querySelector('button[data-product-id]');
    if (!btnAddToCart) return;
    const prodId = btnAddToCart.getAttribute('data-product-id');
    const stored = JSON.parse(localStorage.getItem('wishlist')||'[]');
    if(stored.includes(prodId)) wishBtn.classList.add('wishlisted');
    wishBtn.addEventListener('click', (e)=>{
      e.stopPropagation();
      let list = JSON.parse(localStorage.getItem('wishlist')||'[]');
      if(list.includes(prodId)){
        list = list.filter(id=>id!==prodId);
        wishBtn.classList.remove('wishlisted');
      } else {
        list.push(prodId);
        wishBtn.classList.add('wishlisted');
      }
      localStorage.setItem('wishlist', JSON.stringify(list));
    });
    
    // Quick view handling – simple modal clone of card details
    viewBtn.addEventListener('click', (e)=>{
      e.stopPropagation();
      const modal = document.getElementById('quickViewModal');
      if(!modal) return;
      const img = card.querySelector('img.product-img').src;
      const name = card.querySelector('.card-title').textContent;
      const desc = card.querySelector('.card-text').textContent;
      const price = card.querySelector('.product-price').textContent;
      modal.querySelector('.quick-view-img').src = img;
      modal.querySelector('.quick-view-name').textContent = name;
      modal.querySelector('.quick-view-desc').textContent = desc;
      modal.querySelector('.quick-view-price').textContent = price;
      new bootstrap.Modal(modal).show();
    });
  });
}

/* ------------------------------------------------------------------
   6️⃣ AI Coffee Recommendation – simple questionnaire (kept AI Recommender FAB)
   ------------------------------------------------------------------ */
function initAIRecommender(){
  const fab = document.createElement('button');
  fab.className = 'builder-fab'; 
  fab.style.background = 'linear-gradient(135deg, #e91e63, #c2185b)';
  fab.innerHTML = '<i class="fa-solid fa-robot"></i>';
  const label = document.createElement('span');
  label.className = 'fab-label';
  label.textContent = 'AI Recommender';
  fab.appendChild(label);
  document.body.appendChild(fab);
  fab.addEventListener('click',()=>{ showAIModal(); });
}
function showAIModal(){
  const existing = document.getElementById('aiModal');
  if (existing) { existing.parentElement.remove(); }
  
  const modalHtml = `
  <div class="modal fade" id="aiModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content ai-modal">
        <div class="modal-header border-0">
          <h5 class="modal-title">Find Your Perfect Coffee</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="ai-step-indicator" id="aiSteps"></div>
          <div id="aiContent"></div>
        </div>
      </div>
    </div>
  </div>`;
  const container = document.createElement('div');
  container.innerHTML = modalHtml;
  document.body.appendChild(container);
  const modal = new bootstrap.Modal(document.getElementById('aiModal'));
  modal.show();
  const steps = [
    {question:'Morning or Evening?', options:['Morning','Evening']},
    {question:'Sweet or Strong?', options:['Sweet','Strong']},
    {question:'Hot or Cold?', options:['Hot','Cold']},
    {question:'Mood Today?', options:['Relaxed','Focused','Energized']}
  ];
  let answers = {};
  const stepsContainer = document.getElementById('aiSteps');
  const contentDiv = document.getElementById('aiContent');
  let current = 0;
  function renderStep(){
    stepsContainer.innerHTML='';
    steps.forEach((s,i)=>{
      const dot=document.createElement('div');
      dot.className='ai-step-dot'+(i===current?' active':'');
      stepsContainer.appendChild(dot);
    });
    const step = steps[current];
    contentDiv.innerHTML = `<div class='ai-question'>${step.question}</div>`;
    const opts = document.createElement('div');
    opts.className='d-flex gap-2 flex-wrap';
    step.options.forEach(opt=>{
      const btn=document.createElement('button');
      btn.className='ai-option-btn';
      btn.textContent=opt;
      btn.addEventListener('click',()=>{ answers[step.question]=opt; current++; if(current<steps.length){ renderStep(); } else { showResult(); } });
      opts.appendChild(btn);
    });
    contentDiv.appendChild(opts);
  }
  function showResult(){
    const {"Morning or Evening?": mo, "Sweet or Strong?": ss, "Hot or Cold?": hc, "Mood Today?": mood} = answers;
    let recommendation='Classic Latte';
    if(mo==='Morning' && ss==='Sweet') recommendation='Vanilla Latte';
    if(mo==='Evening' && ss==='Strong') recommendation='Espresso';
    if(hc==='Cold' && ss==='Sweet') recommendation='Iced Caramel Frappe';
    if(mood==='Energized') recommendation='Cold Brew';
    const imgPath = `/images/${recommendation.toLowerCase().replace(/ /g,'_')}.jpg`;
    contentDiv.innerHTML = `<div class='ai-result-card'><img src='${imgPath}' class='ai-result-img' alt='${recommendation}'/><h5 class='mt-3'>${recommendation}</h5><p>Based on your preferences, we think you’ll love this drink.</p></div>`;
  }
  renderStep();
}

/* ------------------------------------------------------------------
   7️⃣ Loyalty Rewards – bindstatic redeem points button
   ------------------------------------------------------------------ */
function initLoyaltyWidget(){
  const redeemBtn = document.getElementById('redeemPointsBtn');
  if (redeemBtn) {
    redeemBtn.addEventListener('click', () => { showLoyaltyModal(); });
  }
}
function showLoyaltyModal(){
  const pointsEl = document.querySelector('#loyaltyRewards .card-text');
  const coins = pointsEl ? parseInt(pointsEl.textContent.trim(), 10) || 0 : 0;
  const nextReward = 100;
  const progress = Math.min(100, (coins/nextReward)*100);
  
  const existing = document.getElementById('loyaltyModal');
  if (existing) { existing.parentElement.remove(); }

  const modalHtml = `
  <div class="modal fade" id="loyaltyModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content loyalty-modal">
        <div class="modal-header border-0">
          <h5 class="modal-title text-gold">Your Loyalty Rewards</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body text-center">
          <div class="loyalty-coins-display" id="coinsDisplay">${coins}</div>
          <p class="text-white-50">Redeem ${nextReward} points for a free classic beverage!</p>
          <div class="loyalty-progress-bar mb-4"><div class="loyalty-progress-fill" style="width:${progress}%"></div></div>
          <button class="btn btn-gold w-100" id="redeemBtn" ${coins>=nextReward?'':'disabled'}>Redeem Reward</button>
        </div>
      </div>
    </div>
  </div>`;
  const container = document.createElement('div');
  container.innerHTML = modalHtml;
  document.body.appendChild(container);
  const modal = new bootstrap.Modal(document.getElementById('loyaltyModal'));
  modal.show();
  
  document.getElementById('redeemBtn').addEventListener('click',()=>{
    // Simple frontend deduct representation or trigger success toast
    if (typeof showToast === 'function') {
      showToast("Redeemed", "Reward successfully redeemed! Present coupon code COFFEEFREE at counter.", "success");
    } else {
      alert('Reward successfully redeemed! Use code COFFEEFREE at checkout.');
    }
    modal.hide();
  });
}

/* ------------------------------------------------------------------
   8️⃣ Birthday Surprise – check stored birthday and show modal
   ------------------------------------------------------------------ */
function initBirthdayFeature(){
  const stored = localStorage.getItem('birthday');
  if(!stored){
    const bday = prompt('Enter your birthday (YYYY-MM-DD) to receive a surprise!');
    if(bday) localStorage.setItem('birthday', bday);
    return;
  }
  const today = new Date().toISOString().split('T')[0];
  if(stored===today){ showBirthdayModal(); }
}
function showBirthdayModal(){
  const modalHtml = `
  <div class="modal fade" id="birthdayModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content birthday-modal">
        <div class="modal-body text-center p-4">
          <div class="birthday-confetti mb-3"><i class="fa-solid fa-birthday-cake"></i></div>
          <h4 class="mb-3">Happy Birthday! 🎉</h4>
          <p class="mb-4">Enjoy a free dessert on us. Use code:</p>
          <div class="coupon-box">BIRTHDAY2026</div>
        </div>
      </div>
    </div>
  </div>`;
  const container = document.createElement('div');
  container.innerHTML = modalHtml;
  document.body.appendChild(container);
  const modal = new bootstrap.Modal(document.getElementById('birthdayModal'));
  modal.show();
}

/* ------------------------------------------------------------------
   9️⃣ Coffee Facts – rotating fact every few seconds
   ------------------------------------------------------------------ */
const coffeeFacts = [
  'Coffee beans are actually seeds of the coffee cherry.',
  'Finland consumes the most coffee per capita in the world.',
  'The word “coffee” originates from the Arabic word “qahwa”.',
  'A single coffee tree produces about 1 to 2 kilograms of coffee annually.',
  'Instant coffee was invented in 1906 by Satori Kato.'
];
function startCoffeeFacts(){
  const ticker = document.createElement('div');
  ticker.className = 'facts-ticker slide-up-on-scroll';
  ticker.innerHTML = `<i class="fa-solid fa-coffee fact-icon"></i><span class="facts-text">${coffeeFacts[0]}</span>`;
  const container = document.querySelector('.container');
  if(container){ container.appendChild(ticker); }
  let idx = 0;
  setInterval(()=>{
    const txt = ticker.querySelector('.facts-text');
    if (txt) {
      txt.classList.add('fading');
      setTimeout(()=>{ idx = (idx+1)%coffeeFacts.length; txt.textContent = coffeeFacts[idx]; txt.classList.remove('fading'); },600);
    }
  },6000);
}

/* ------------------------------------------------------------------
   🔟 Instagram Gallery – static grid using existing product images
   ------------------------------------------------------------------ */
function initInstaGallery(){
  const section = document.createElement('section');
  section.className = 'mt-5';
  const title = document.createElement('h3');
  title.className = 'premium-section-title';
  title.textContent = 'Instagram Gallery';
  const grid = document.createElement('div');
  grid.className = 'insta-grid';
  const imgs = document.querySelectorAll('.product-img');
  for(let i=0;i<12 && i<imgs.length;i++){
    const item = document.createElement('div');
    item.className = 'insta-item';
    const img = document.createElement('img');
    img.src = imgs[i].src;
    item.appendChild(img);
    const overlay = document.createElement('div');
    overlay.className = 'insta-item-overlay';
    overlay.innerHTML = '<i class="fa-solid fa-instagram"></i>';
    item.appendChild(overlay);
    grid.appendChild(item);
  }
  section.appendChild(title);
  section.appendChild(grid);
  const container = document.querySelector('.container');
  if(container){ container.appendChild(section); }
}

/* ------------------------------------------------------------------
   11) Store Information – hours, location, map placeholder
   ------------------------------------------------------------------ */
function initStoreInfo(){
  const section = document.createElement('section');
  section.className = 'mt-5';
  const title = document.createElement('h3');
  title.className = 'premium-section-title';
  title.textContent = 'Our Store';
  const card = document.createElement('div');
  card.className = 'store-info-card';
  card.innerHTML = `
    <h5 class='mb-3 text-gold'>Coffee & Co Café</h5>
    <p class='mb-2 text-white-50'><i class='fa-solid fa-map-marker-alt me-2 text-gold'></i>123 Premium Bean Avenue, Coffee District</p>
    <p class='mb-2 text-white-50'><i class='fa-solid fa-phone me-2 text-gold'></i>+1 (555) 456-7890</p>
    <p class='mb-4 text-white-50'><i class='fa-solid fa-envelope me-2 text-gold'></i>support@coffeeandco.com</p>
    <div class='store-hours mb-3'>
      <h6 class='mb-2 text-gold'>Opening Hours</h6>
      <div class='store-hours-row'><span class='day'>Mon‑Fri</span><span class='hours'>08:00 – 22:00</span></div>
      <div class='store-hours-row'><span class='day'>Sat</span><span class='hours'>09:00 – 23:00</span></div>
      <div class='store-hours-row'><span class='day'>Sun</span><span class='hours'>10:00 – 20:00</span></div>
    </div>
    <a href="#" class='btn btn-outline-gold w-100'>View on Map</a>
  `;
  section.appendChild(title);
  section.appendChild(card);
  const container = document.querySelector('.container');
  if(container){ container.appendChild(section); }
}

/* ------------------------------------------------------------------
   12) Live Order Tracking – demo modal with timeline steps
   ------------------------------------------------------------------ */
function initOrderTracking(){
  const btn = document.createElement('button');
  btn.className = 'btn btn-outline-gold ms-2';
  btn.innerHTML = '<i class="fa-solid fa-truck-fast"></i> Track Order';
  const nav = document.querySelector('.navbar-coffee .d-flex.align-items-center');
  if(nav){ nav.appendChild(btn); }
  btn.addEventListener('click',()=>{ showTrackingModal(); });
}
function showTrackingModal(){
  const modalHtml = `
  <div class="modal fade" id="trackingModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content tracking-modal">
        <div class="modal-header border-0">
          <h5 class="modal-title text-gold">Order Tracking</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="tracking-timeline">
            <div class="tracking-step done">
              <div class="tracking-step-icon"><i class="fa-solid fa-check"></i></div>
              <div class="tracking-step-label">Preparing</div>
            </div>
            <div class="tracking-step active">
              <div class="tracking-step-icon"><i class="fa-solid fa-coffee-bean"></i></div>
              <div class="tracking-step-label">Brewing</div>
            </div>
            <div class="tracking-step pending">
              <div class="tracking-step-icon"><i class="fa-solid fa-box"></i></div>
              <div class="tracking-step-label">Packed</div>
            </div>
            <div class="tracking-step pending">
              <div class="tracking-step-icon"><i class="fa-solid fa-truck"></i></div>
              <div class="tracking-step-label">Out for Delivery</div>
            </div>
            <div class="tracking-step pending">
              <div class="tracking-step-icon"><i class="fa-solid fa-door-open"></i></div>
              <div class="tracking-step-label">Delivered</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>`;
  const container = document.createElement('div');
  container.innerHTML = modalHtml;
  document.body.appendChild(container);
  const modal = new bootstrap.Modal(document.getElementById('trackingModal'));
  modal.show();
}

/* ------------------------------------------------------------------
   13) Customer Reviews – static cards with dummy data
   ------------------------------------------------------------------ */
function initCustomerReviews(){
  const section = document.createElement('section');
  section.className = 'reviews-section mt-5';
  const title = document.createElement('h3');
  title.className = 'premium-section-title';
  title.textContent = 'Customer Reviews';
  const row = document.createElement('div');
  row.className = 'row g-4';
  const dummy = [
    {name:'Aisha K.', rating:5, text:'The latte art is pure magic! Highly recommended.', avatar:'A'},
    {name:'Rohan P.', rating:4, text:'Great ambiance and excellent coffee quality.', avatar:'R'},
    {name:'Lina M.', rating:5, text:'I love the seasonal flavors – always a pleasant surprise.', avatar:'L'},
    {name:'Carlos S.', rating:4, text:'Friendly staff and quick service.', avatar:'C'}
  ];
  dummy.forEach(d=>{
    const col = document.createElement('div');
    col.className = 'col-md-6';
    const card = document.createElement('div');
    card.className = 'review-card h-100';
    card.innerHTML = `
      <div class='d-flex align-items-center mb-3'>
        <div class='review-avatar me-3'>${d.avatar}</div>
        <div>
          <div class='review-name'>${d.name}</div>
          <div class='review-stars'>${'★'.repeat(d.rating)}${'☆'.repeat(5-d.rating)}</div>
        </div>
      </div>
      <p class='review-text'>${d.text}</p>`;
    col.appendChild(card);
    row.appendChild(col);
  });
  section.appendChild(title);
  section.appendChild(row);
  const container = document.querySelector('.container');
  if(container){ container.appendChild(section); }
}

/* ------------------------------------------------------------------
   14) Today's Special – mapped to our custom controller route
   ------------------------------------------------------------------ */
function initTodaySpecial(){
  const products = document.querySelectorAll('.product-card-wrapper');
  if(!products.length) return;
  const randomCard = products[Math.floor(Math.random()*products.length)];
  const img = randomCard.querySelector('.product-img').src;
  const name = randomCard.querySelector('.card-title').textContent.trim();
  const priceText = randomCard.querySelector('.product-price').textContent.trim();
  const price = parseFloat(priceText.replace('₹','')) || 0;
  const discount = (price * 0.2).toFixed(2);
  const finalPrice = (price - discount).toFixed(2);
  
  const btnAddToCart = randomCard.querySelector('.btn-add-to-cart');
  if (!btnAddToCart) return;
  const productId = btnAddToCart.getAttribute('data-product-id');
  
  const section = document.getElementById('today-special');
  if(!section) return;
  const endTime = Date.now() + 24*60*60*1000;
  section.innerHTML = `
    <div class="today-special-card glass-card p-4 d-flex align-items-center" style="gap:20px;">
      <img src="${img}" alt="${name}" class="img-fluid" style="max-width:200px; border-radius:12px;"/>
      <div>
        <h3 class="text-gold mb-2">Today's Special: ${name}</h3>
        <p class="mb-1"><s>₹${price}</s> <span class="text-success">₹${finalPrice}</span> (Save ₹${discount})</p>
        <p class="mb-2">Ends in <span id="todaySpecialTimer"></span></p>
        <a href="/product/order/${productId}" class="btn btn-gold">Order Now</a>
      </div>
    </div>`;
  const timerEl = document.getElementById('todaySpecialTimer');
  const updateTimer = () => {
    const now = Date.now();
    const diff = endTime - now;
    if(diff<=0){ timerEl.textContent='Expired'; return; }
    const h = Math.floor(diff/3600000);
    const m = Math.floor((diff%3600000)/60000);
    const s = Math.floor((diff%60000)/1000);
    timerEl.textContent = `${h.toString().padStart(2,'0')}:${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}`;
  };
  setInterval(updateTimer,1000);
  updateTimer();
}

/* ------------------------------------------------------------------
   15) Combo Deals – uses /cart/add-combo AJAX request
   ------------------------------------------------------------------ */
function initComboDeals(){
  const v = Date.now();
  const base = window.location.origin;
  const combos = [
    {title:'Cappuccino + Garlic Bread',    item1:'Cappuccino',        item2:'Garlic Bread',      img1:`${base}/images/combos/cappuccino.jpg?v=${v}`,        img2:`${base}/images/combos/garlic_bread.jpg?v=${v}`,        price:250, discount:30},
    {title:'Espresso + Brownie',           item1:'Espresso',          item2:'Brownie',           img1:`${base}/images/combos/espresso.jpg?v=${v}`,           img2:`${base}/images/combos/brownie.jpg?v=${v}`,             price:220, discount:25},
    {title:'Latte + Chocolate Croissant',  item1:'Latte',             item2:'Chocolate Croissant', img1:`${base}/images/combos/latte.jpg?v=${v}`,              img2:`${base}/images/combos/chocolate_croissant.jpg?v=${v}`, price:270, discount:35},
    {title:'Mocha + Cheese Tart',          item1:'Mocha',             item2:'Cheese Tart',       img1:`${base}/images/combos/mocha.jpg?v=${v}`,              img2:`${base}/images/combos/cheese_tart.jpg?v=${v}`,         price:300, discount:40},
    {title:'Americano + Blueberry Muffin', item1:'Americano',         item2:'Blueberry Muffin',  img1:`${base}/images/combos/americano.jpg?v=${v}`,          img2:`${base}/images/combos/blueberry_muffin.jpg?v=${v}`,    price:210, discount:20},
    {title:'Flat White + Scone',           item1:'Flat White',        item2:'Scone',             img1:`${base}/images/combos/flat_white.jpg?v=${v}`,         img2:`${base}/images/combos/scone.jpg?v=${v}`,               price:230, discount:28},
    {title:'Cold Brew + Chocolate Cookie', item1:'Cold Brew',         item2:'Choc Cookie',       img1:`${base}/images/combos/cold_brew.jpg?v=${v}`,          img2:`${base}/images/combos/chocolate_cookie.jpg?v=${v}`,    price:260, discount:33},
    {title:'Tea + Lemon Cake',             item1:'Tea',               item2:'Lemon Cake',        img1:`${base}/images/combos/tea.jpg?v=${v}`,                img2:`${base}/images/combos/lemon_cake.jpg?v=${v}`,          price:240, discount:30}
  ];
  const section = document.getElementById('combo-deals');
  if(!section) return;
  const fallback = 'https://placehold.co/120x120/c8a96e/ffffff?text=☕';
  const onErr = `this.onerror=null;this.src='${fallback}';`;
  let html = '<div class="row g-4">';
  combos.forEach((c, idx)=>{
    const finalPrice = c.price - c.discount;
    html += `
      <div class="col-sm-6 col-lg-3">
        <div class="card combo-card glass-card h-100 p-3" style="border-radius:16px;">
          <div class="d-flex justify-content-center align-items-center gap-2 mb-3" style="min-height:130px;">
            <div class="text-center" style="flex:1;">
              <img src="${c.img1}" onerror="${onErr}"
                style="width:110px;height:110px;object-fit:cover;border-radius:12px;box-shadow:0 4px 12px rgba(0,0,0,.15);"
                alt="${c.item1}"/>
              <div style="font-size:0.7rem;margin-top:4px;color:#6b4c2a;font-weight:600;">${c.item1}</div>
            </div>
            <div style="font-size:1.4rem;color:#c8a96e;font-weight:bold;">+</div>
            <div class="text-center" style="flex:1;">
              <img src="${c.img2}" onerror="${onErr}"
                style="width:110px;height:110px;object-fit:cover;border-radius:12px;box-shadow:0 4px 12px rgba(0,0,0,.15);"
                alt="${c.item2}"/>
              <div style="font-size:0.7rem;margin-top:4px;color:#6b4c2a;font-weight:600;">${c.item2}</div>
            </div>
          </div>
          <h5 class="card-title text-center text-gold mb-1" style="font-size:0.95rem;">${c.title}</h5>
          <p class="text-center mb-2" style="font-size:0.85rem;"><s style="color:#999;">₹${c.price}</s> <strong style="color:#2e7d32;">₹${finalPrice}</strong> <span class="badge" style="background:#fff3cd;color:#856404;font-size:0.7rem;">Save ₹${c.discount}</span></p>
          <div class="d-grid mt-auto"><button class="btn btn-gold btn-add-combo" data-index="${idx}" style="font-size:0.85rem;">Add Combo to Cart</button></div>
        </div>
      </div>`;
  });
  html += '</div>';
  section.innerHTML = html;

  // Click listeners to call add-combo route via Ajax
  section.querySelectorAll('.btn-add-combo').forEach(btn => {
    btn.addEventListener('click', () => {
      const idx = btn.getAttribute('data-index');
      const combo = combos[idx];
      const formData = new FormData();
      formData.append('item1', combo.item1);
      formData.append('item2', combo.item2);

      fetch('/cart/add-combo', {
        method: 'POST',
        body: formData
      })
      .then(response => response.text())
      .then(data => {
        if (data.startsWith('SUCCESS:')) {
          const newCount = data.split(':')[1];
          const badges = document.querySelectorAll('.nav-badge');
          badges.forEach(badge => {
            badge.textContent = newCount;
            badge.classList.remove('d-none');
          });
          if (typeof showToast === 'function') {
            showToast("Combo Added", `Successfully added ${combo.title} to your cart!`, "success");
          } else {
            alert(`${combo.title} added to cart!`);
          }
        } else {
          if (typeof showToast === 'function') {
            showToast("Error", data.replace('ERROR:', ''), "warning");
          } else {
            alert(data);
          }
        }
      })
      .catch(error => {
        console.error('Error adding combo to cart:', error);
        if (typeof showToast === 'function') {
          showToast("Error", "Could not add combo to cart. Please try again.", "danger");
        } else {
          alert("Error adding combo to cart.");
        }
      });
    });
  });
}

/* ------------------------------------------------------------------
   16) Buy 2 Get 1 Free Promo Banner
   ------------------------------------------------------------------ */
function initB2G1Banner(){
  const section = document.getElementById('b2g1-banner');
  if(!section) return;
  const endTime = Date.now() + 48*60*60*1000;
  const html = `
    <div class="b2g1-banner glass-card p-4 text-center position-relative" style="overflow:hidden;">
      <h4 class="text-gold mb-2">Buy 2 Get 1 Free! 🎁</h4>
      <p class="mb-2">Select any two items and get the third of equal or lower value for free.</p>
      <p>Offer ends in <span id="b2g1Timer"></span></p>
      <a href="/shop" class="btn btn-outline-gold mt-2">Shop Now</a>
      <div class="floating-bean" style="position:absolute; top:10px; left:-30px; font-size:2rem; animation:beanFloat 5s infinite;">☕</div>
    </div>`;
  section.innerHTML = html;
  const timerEl = document.getElementById('b2g1Timer');
  const update = () => {
    const diff = endTime - Date.now();
    if(diff<=0){ timerEl.textContent='Expired'; return; }
    const d = Math.floor(diff/(1000*60*60*24));
    const h = Math.floor((diff%(1000*60*60*24))/(1000*60*60));
    const m = Math.floor((diff%(1000*60*60))/ (1000*60));
    const s = Math.floor((diff%(1000*60))/1000);
    timerEl.textContent = `${d}d ${h}h ${m}m ${s}s`;
  };
  setInterval(update,1000);
  update();
}

/* ------------------------------------------------------------------
   17) Real HTML5 Canvas Scratch Card
   ------------------------------------------------------------------ */
function initScratchCard(){
  const section = document.getElementById('scratch-card');
  if(!section) return;

  const rewards = [
    '10% OFF', '15% OFF', '20% OFF',
    '₹50 OFF', '₹100 OFF',
    'Free Cappuccino', 'Free Brownie', 'Free Cookie',
    'Buy 1 Get 1 Coffee', 'Better Luck Next Time'
  ];

  const reward = rewards[Math.floor(Math.random() * rewards.length)];

  section.innerHTML = `
    <div class="scratch-card glass-card p-4 text-center">
      <h4 class="text-gold mb-3">Scratch &amp; Win</h4>
      <div class="scratch-container" style="position: relative; width: 300px; height: 150px; margin: auto; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.15);">
        <!-- Underneath prize text -->
        <div class="scratch-prize" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #fff; border-radius: 12px; font-weight: bold; font-size: 1.5rem; color: #3e2723; border: 2px dashed #c8a96e; box-sizing: border-box; z-index: 1; user-select: none;">
          <span style="font-size: 0.8rem; text-transform: uppercase; color: #8d6e63; letter-spacing: 1px;">Your Prize</span>
          <span id="prizeText">${reward}</span>
        </div>
        <!-- Top canvas coating -->
        <canvas id="scratchCanvas" width="300" height="150" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border-radius: 12px; cursor: crosshair; z-index: 2; touch-action: none;"></canvas>
      </div>
      <p id="scratchResult" class="mt-3" style="font-weight: 600; color: #2e7d32; min-height: 24px;"></p>
      <button id="playAgainBtn" class="btn btn-gold mt-2" style="display: none;">Play Again</button>
    </div>`;

  const canvas = document.getElementById('scratchCanvas');
  const ctx = canvas.getContext('2d');

  // Draw silver-grey coating
  ctx.fillStyle = '#b0bec5';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // Draw nice grid/pattern on coating
  ctx.fillStyle = '#90a4ae';
  for (let i = 0; i < canvas.width; i += 20) {
    ctx.fillRect(i, 0, 1, canvas.height);
  }
  for (let j = 0; j < canvas.height; j += 20) {
    ctx.fillRect(0, j, canvas.width, 1);
  }

  // Write "Scratch Here" text
  ctx.fillStyle = '#37474f';
  ctx.font = 'bold 16px sans-serif';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText('SCRATCH HERE', canvas.width / 2, canvas.height / 2);

  let isDrawing = false;
  let revealed = false;
  let lastX = 0;
  let lastY = 0;

  function getCoords(e) {
    const rect = canvas.getBoundingClientRect();
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;
    return {
      x: clientX - rect.left,
      y: clientY - rect.top
    };
  }

  function startDrawing(e) {
    if (revealed) return;
    isDrawing = true;
    const coords = getCoords(e);
    lastX = coords.x;
    lastY = coords.y;
  }

  function draw(e) {
    if (!isDrawing || revealed) return;
    e.preventDefault();
    const coords = getCoords(e);

    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.moveTo(lastX, lastY);
    ctx.lineTo(coords.x, coords.y);
    ctx.lineWidth = 40;
    ctx.lineCap = 'round';
    ctx.stroke();

    lastX = coords.x;
    lastY = coords.y;

    checkScratchPercentage();
  }

  function stopDrawing() {
    isDrawing = false;
  }

  function checkScratchPercentage() {
    if (revealed) return;
    const imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    const pixels = imgData.data;
    let transparent = 0;
    for (let i = 3; i < pixels.length; i += 4) {
      if (pixels[i] === 0) {
        transparent++;
      }
    }
    const percent = (transparent / (pixels.length / 4)) * 100;
    if (percent >= 50) {
      revealPrize();
    }
  }

  function revealPrize() {
    revealed = true;
    isDrawing = false;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const resultEl = document.getElementById('scratchResult');
    if (reward === 'Better Luck Next Time') {
      resultEl.style.color = '#c62828';
      resultEl.textContent = 'Oops! Better luck next time!';
    } else {
      resultEl.style.color = '#2e7d32';
      resultEl.textContent = `Congratulations! You won: ${reward}`;

      if (window.confetti) {
        window.confetti({ particleCount: 150, spread: 80, origin: { y: 0.6 } });
      }
    }

    document.getElementById('playAgainBtn').style.display = 'inline-block';
  }

  // Mouse events
  canvas.addEventListener('mousedown', startDrawing);
  canvas.addEventListener('mousemove', draw);
  canvas.addEventListener('mouseup', stopDrawing);
  canvas.addEventListener('mouseleave', stopDrawing);

  // Touch events
  canvas.addEventListener('touchstart', startDrawing);
  canvas.addEventListener('touchmove', draw);
  canvas.addEventListener('touchend', stopDrawing);
  canvas.addEventListener('touchcancel', stopDrawing);

  document.getElementById('playAgainBtn').addEventListener('click', () => {
    initScratchCard();
  });
}

/* ------------------------------------------------------------------
   🎉 Initialize all features after DOM ready
   ------------------------------------------------------------------ */
document.addEventListener('DOMContentLoaded',()=>{
  showQuote();
  initOffersBanner();
  initDarkMode();
  markSpecialProducts();
  initProductOverlays();
  initTodaySpecial();
  initComboDeals();
  initB2G1Banner();
  initScratchCard();
  initAIRecommender();
  initLoyaltyWidget();
  initBirthdayFeature();
  startCoffeeFacts();
  initInstaGallery();
  initStoreInfo();
  initOrderTracking();
  initCustomerReviews();
});
