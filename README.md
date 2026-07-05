# CAM2COOK

CAM2COOK is a landing page and prototype experience for an AI-powered cooking assistant. The concept combines computer vision, ingredient recognition, and real-time cooking guidance so a user can point a camera at their fridge or kitchen setup and receive helpful recipe and cooking instructions.

## Overview

The project currently includes:

- A polished landing page for the CAM2COOK concept
- An animated hero experience with scan, decide, and guide scenes
- Feature, how-it-works, demo, testimonials, FAQ, and CTA sections
- Placeholder pages for early access, demo, video, privacy, terms, and contact
- Light/dark theme support with a toggle

## Project Structure

The frontend now lives in the following folder:

- [frontend/src/index.html](frontend/src/index.html) — main landing page
- [frontend/src/css/styles.css](frontend/src/css/styles.css) — visual design, layout, animations, and theme styling
- [frontend/src/js/script.js](frontend/src/js/script.js) — interactive behavior such as animations, theme switching, demo simulation, and smooth scrolling
- [frontend/src/early-access.html](frontend/src/early-access.html) — placeholder early access page
- [frontend/src/demo.html](frontend/src/demo.html) — placeholder demo page
- [frontend/src/video.html](frontend/src/video.html) — placeholder video page
- [frontend/src/privacy.html](frontend/src/privacy.html) — privacy placeholder page
- [frontend/src/terms.html](frontend/src/terms.html) — terms placeholder page
- [frontend/src/contact.html](frontend/src/contact.html) — contact placeholder page

## Run Locally

From the repository root, start a simple local server:

```bash
python -m http.server 8000
```

Then open:

```text
http://localhost:8000/frontend/src/
```

You can also open [frontend/src/index.html](frontend/src/index.html) directly in a browser.

## Tech Stack

- HTML5
- CSS3
- JavaScript (Vanilla)

## Notes

This project is currently a front-end prototype and UI experience. The AI functionality and full backend integration are planned for future development.

## Future Goals

- Add real ingredient recognition using computer vision
- Connect recipes to a live recommendation engine
- Add step-by-step cooking guidance from camera input
- Build a complete interactive app experience
