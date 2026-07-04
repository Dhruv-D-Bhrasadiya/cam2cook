// ============ HERO ANIMATION ============
const scenes = document.querySelectorAll(".scene");
const dots = document.querySelectorAll(".dot");
let currentScene = 1;
let sceneInterval;

function showScene(sceneNumber) {
  scenes.forEach((scene) => scene.classList.remove("active"));
  dots.forEach((dot) => dot.classList.remove("active"));

  const activeScene = document.querySelector(`.scene-${sceneNumber}`);
  if (activeScene) {
    activeScene.classList.add("active");
  }

  const activeDot = document.querySelector(`.dot[data-scene="${sceneNumber}"]`);
  if (activeDot) {
    activeDot.classList.add("active");
  }

  currentScene = sceneNumber;
}

function nextScene() {
  let next = currentScene + 1;
  if (next > 3) next = 1;
  showScene(next);
}

function startSceneAnimation() {
  sceneInterval = setInterval(nextScene, 4000);
}

function stopSceneAnimation() {
  clearInterval(sceneInterval);
}

// Initialize scene animation
showScene(1);
startSceneAnimation();

// Pause on hover, resume on mouse leave
const animationContainer = document.querySelector(".animation-container");
if (animationContainer) {
  animationContainer.addEventListener("mouseenter", stopSceneAnimation);
  animationContainer.addEventListener("mouseleave", startSceneAnimation);
}

// Click dots to change scene
dots.forEach((dot) => {
  dot.addEventListener("click", () => {
    const sceneNum = dot.getAttribute("data-scene");
    showScene(sceneNum);
    stopSceneAnimation();
    startSceneAnimation();
  });
});

// ============ THEME TOGGLE ============
const themeToggle = document.querySelector(".theme-toggle");
const htmlElement = document.documentElement;

// Initialize theme from localStorage
function initTheme() {
  const savedTheme = localStorage.getItem("cam2cook-theme") || "light";
  if (savedTheme === "light") {
    document.body.classList.add("light-mode");
    themeToggle.textContent = "🌙";
  } else {
    document.body.classList.remove("light-mode");
    themeToggle.textContent = "☀️";
  }
}

// Toggle theme on button click
if (themeToggle) {
  themeToggle.addEventListener("click", () => {
    const isLightMode = document.body.classList.toggle("light-mode");
    localStorage.setItem("cam2cook-theme", isLightMode ? "light" : "dark");
    themeToggle.textContent = isLightMode ? "🌙" : "☀️";
    themeToggle.style.animation = "none";
    setTimeout(() => {
      themeToggle.style.animation = "";
    }, 10);
  });
  
  // Initialize theme on page load
  initTheme();
}

// ============ DEMO SIMULATION ============
const simulateBtn = document.getElementById("simulateBtn");
const demoScreen = document.getElementById("demoScreen");

if (simulateBtn && demoScreen) {
  simulateBtn.addEventListener("click", () => {
    // Clear previous content
    demoScreen.innerHTML = "";

  // Create demo video/content
  const demoContent = document.createElement("div");
  demoContent.style.cssText = `
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 2rem;
        padding: 2rem;
        text-align: center;
    `;

  // Simulate kitchen camera feed
  const videoArea = document.createElement("div");
  videoArea.style.cssText = `
        width: 100%;
        flex: 1;
        background: linear-gradient(135deg, #2a2a2a 0%, #1a1a1a 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 1.2rem;
        position: relative;
        overflow: hidden;
    `;

  // Camera feed representation
  videoArea.innerHTML = `
        <div style="text-align: center;">
            <div style="font-size: 3rem; margin-bottom: 1rem;">📹</div>
            <p>Kitchen Camera Feed</p>
            <div style="margin-top: 1rem; opacity: 0.7;">
                <div>🍳 Stove: Active</div>
                <div>✋ Hands Detected</div>
                <div>🔪 Cutting Board</div>
            </div>
        </div>
        <div style="position: absolute; top: 10px; right: 10px; background: rgba(255,107,107,0.8); color: white; padding: 0.5rem 1rem; border-radius: 5px; font-weight: bold; font-size: 0.9rem;">
            RECORDING
        </div>
    `;

  // AR Guidance overlay
  const guidance = document.createElement("div");
  guidance.style.cssText = `
        width: 100%;
        background: rgba(79, 172, 196, 0.2);
        border: 2px solid #4ECDC4;
        border-radius: 8px;
        padding: 1rem;
        color: #4ECDC4;
        font-weight: 600;
        animation: slideUp 0.5s ease-out;
    `;

  const guidanceText = [
    "✓ Stove heat is perfect!",
    "✓ Ingredients arranged correctly",
    "✓ Hand position optimal",
    "Next: Stir for 30 seconds",
  ];

  const randomGuidance =
    guidanceText[Math.floor(Math.random() * guidanceText.length)];
  guidance.textContent = randomGuidance;

  demoContent.appendChild(videoArea);
  demoContent.appendChild(guidance);
  demoScreen.appendChild(demoContent);

  // Auto-dismiss after 5 seconds
  setTimeout(() => {
    demoScreen.innerHTML =
      '<div class="demo-placeholder"><p>Click below to simulate camera feed</p></div>';
  }, 5000);
  });
}

// ============ SMOOTH SCROLLING ============
document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener("click", function (e) {
    e.preventDefault();
    const target = document.querySelector(this.getAttribute("href"));
    if (target) {
      target.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  });
});

// ============ EMAIL NOTIFICATION SIGNUP ============
const emailInput = document.querySelector(".email-input");
const getCTAButton = document.querySelector(".final-cta .btn-primary");

if (getCTAButton && emailInput) {
  getCTAButton.addEventListener("click", () => {
    const email = emailInput.value.trim();

    if (!email) {
      alert("Please enter your email address");
      emailInput.focus();
      return;
    }

    if (!isValidEmail(email)) {
      alert("Please enter a valid email address");
      emailInput.focus();
      return;
    }

    // Success message
    const originalText = getCTAButton.textContent;
    getCTAButton.textContent = "✓ Added! We'll notify you soon";
    getCTAButton.style.background = "#4ECDC4";

    // Reset after 3 seconds
    setTimeout(() => {
      getCTAButton.textContent = originalText;
      getCTAButton.style.background = "";
      emailInput.value = "";
    }, 3000);
  });
}

function isValidEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

// ============ FAQ ACCORDION ============
const faqItems = document.querySelectorAll(".faq-item");

faqItems.forEach((item) => {
  item.addEventListener("click", () => {
    // Toggle active state (optional)
    item.style.transform =
      item.style.transform === "scale(1.05)" ? "scale(1)" : "scale(1.05)";
  });
});

// ============ NAVBAR SCROLL EFFECT ============
const navbar = document.querySelector(".navbar");
let lastScrollTop = 0;

if (navbar) {
  window.addEventListener("scroll", () => {
    let scrollTop = window.pageYOffset || document.documentElement.scrollTop;

    if (scrollTop > 100) {
      navbar.style.boxShadow = "0 10px 30px rgba(0, 0, 0, 0.3)";
    } else {
      navbar.style.boxShadow = "none";
    }

    lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
  });
}

// ============ INTERSECTION OBSERVER FOR ANIMATIONS ============
const observerOptions = {
  threshold: 0.1,
  rootMargin: "0px 0px -50px 0px",
};

const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      entry.target.style.opacity = "1";
      entry.target.style.transform = "translateY(0)";
    }
  });
}, observerOptions);

// Observe feature cards and other elements
document
  .querySelectorAll(
    ".feature-card, .testimonial-card, .faq-item, .timeline-item",
  )
  .forEach((el) => {
    el.style.opacity = "0";
    el.style.transform = "translateY(20px)";
    el.style.transition = "all 0.6s ease-out";
    observer.observe(el);
  });

// ============ BUTTON RIPPLE EFFECT ============
const buttons = document.querySelectorAll(".btn");

buttons.forEach((button) => {
  button.addEventListener("click", function (e) {
    const ripple = document.createElement("span");
    const rect = this.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = e.clientX - rect.left - size / 2;
    const y = e.clientY - rect.top - size / 2;

    ripple.style.cssText = `
            position: absolute;
            width: ${size}px;
            height: ${size}px;
            background: rgba(255, 255, 255, 0.5);
            border-radius: 50%;
            left: ${x}px;
            top: ${y}px;
            pointer-events: none;
            animation: rippleEffect 0.6s ease-out;
        `;

    this.style.position = "relative";
    this.style.overflow = "hidden";
    this.appendChild(ripple);

    setTimeout(() => ripple.remove(), 600);
  });
});

// Add ripple animation to CSS dynamically if not present
if (!document.querySelector("style[data-ripple]")) {
  const style = document.createElement("style");
  style.setAttribute("data-ripple", "true");
  style.textContent = `
        @keyframes rippleEffect {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
    `;
  document.head.appendChild(style);
}

// ============ KEYBOARD NAVIGATION ============
document.addEventListener("keydown", (e) => {
  // Arrow keys to navigate hero scenes
  if (e.key === "ArrowRight") {
    nextScene();
    stopSceneAnimation();
    startSceneAnimation();
  }
  if (e.key === "ArrowLeft") {
    let prev = currentScene - 1;
    if (prev < 1) prev = 3;
    showScene(prev);
    stopSceneAnimation();
    startSceneAnimation();
  }
});

// ============ MOBILE NAVIGATION MENU ============
// Create mobile menu toggle if on small screens
function setupMobileMenu() {
  if (window.innerWidth <= 768) {
    const navbar = document.querySelector(".navbar");

    // Check if menu toggle already exists
    if (!document.querySelector(".menu-toggle")) {
      const toggle = document.createElement("button");
      toggle.className = "menu-toggle";
      toggle.textContent = "☰";
      toggle.style.cssText = `
                display: none;
                background: transparent;
                border: none;
                color: var(--text);
                font-size: 1.5rem;
                cursor: pointer;
                padding: 0.5rem;
            `;

      navbar.querySelector(".logo").parentElement.appendChild(toggle);
    }
  }
}

setupMobileMenu();
window.addEventListener("resize", setupMobileMenu);

// ============ LAZY LOADING IMAGES ============
// Placeholder for future image optimization
const images = document.querySelectorAll("img");
if ("IntersectionObserver" in window) {
  const imageObserver = new IntersectionObserver((entries, observer) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        const img = entry.target;
        if (img.dataset.src) {
          img.src = img.dataset.src;
          img.removeAttribute("data-src");
        }
        observer.unobserve(img);
      }
    });
  });

  images.forEach((img) => imageObserver.observe(img));
}

console.log("CAM2COOK website loaded successfully! 🍳");
