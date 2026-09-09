# CAM2COOK 🍳📸

> **See what you have. Know when it expires. Decide what to cook. Get guided from camera-on to ready-to-serve.**

CAM2COOK is an AI-powered **food inventory, freshness tracking, recipe recommendation, and real-time cooking companion** for homes, shared households, and hospitality teams.

The long-term vision is to make managing food feel as simple as tracking expenses: instead of manually recording every item, a user captures a photo or video of their fridge, pantry, groceries, or kitchen. Computer vision identifies the food items, the system maintains an inventory and estimates a configurable freshness/use-by window, and the app turns that inventory into timely actions—**use it, cook it, share it, or restock it**.

The experience continues beyond inventory. When an ingredient is close to its deadline, CAM2COOK can recommend recipes that prioritize the food at risk, show what additional ingredients are required, and then become a **real-time AI sous-chef**. The user can place the phone where the camera sees the cooking surface while CAM2COOK uses vision, audio, and recipe state to guide the user step by step—from washing and cutting ingredients to frying, baking, cooking, plating, and serving.

---

## The Problem

Food management breaks down in three places:

1. **People forget what they already own.** Ingredients get buried in refrigerators, drawers, and cupboards.
2. **People do not know what is about to become unusable.** Expiration and freshness are difficult to track, especially for fresh produce and opened ingredients.
3. **People know they should cook something but do not know what or how.** Recipes assume the user can interpret instructions, while real cooking often requires continuous feedback.

CAM2COOK connects these moments into one workflow:

```text
CAPTURE → RECOGNIZE → INVENTORY → TRACK → WARN → DECIDE → COOK → VERIFY → SERVE
```

---

## Product Vision

CAM2COOK aims to become a **visual food operating system** for the kitchen.

### 1. Visual Food Inventory

Capture one or multiple food items using a phone camera. The vision pipeline detects and classifies ingredients, packaged goods, groceries, and other relevant kitchen inventory.

The user can then review and correct the recognition before saving it to their inventory.

### 2. Freshness & Deadline Tracking

Every inventory item can have:

- Purchase/opened/captured timestamp
- Estimated freshness window
- User-defined deadline
- Storage context such as refrigerator, freezer, pantry, or room temperature
- Quantity/unit
- Freshness confidence
- Notes and status

The system should distinguish between **estimated freshness** and a known manufacturer/use-by date. Computer vision should assist with estimation rather than pretending it can guarantee food safety.

### 3. Smart Waste Prevention

CAM2COOK surfaces items based on urgency:

```text
Fresh → Use Soon → At Risk → Deadline Today → Overdue / Review
```

Notifications can suggest actions such as:

- Cook it today
- Add it to a planned meal
- Freeze/preserve it when appropriate
- Share it with a household member
- Mark it consumed
- Review whether it is still suitable to use

### 4. Recipe Intelligence

When the user asks **“What can I make?”**, CAM2COOK reasons over current inventory and preferences.

Recipes can be ranked by:

- Ingredients that are closest to their deadline
- Ingredient availability
- Missing ingredients
- Cooking time
- Difficulty
- Cuisine
- Dietary preferences
- Allergies and exclusions
- Equipment available
- Number of servings
- User history and preferences

For example:

> **Chicken is best used today.**
>
> Make **Chicken Stir-Fry in 20 minutes**.
>
> You already have: chicken, onion, garlic, capsicum, soy sauce.
>
> You are missing: sesame oil.

### 5. Real-Time AI Cooking Companion

This is the deeper vision of CAM2COOK.

Instead of displaying a static recipe, the user starts **Cook Mode**, places the phone/tripod so the camera can see the workspace, and the AI observes the cooking process.

The user can talk naturally:

> “What do I do next?”
>
> “Is this onion cut small enough?”
>
> “Should I lower the heat?”
>
> “How long should I cook this?”

CAM2COOK combines:

- Live camera input
- Voice input/output
- Recipe state
- Ingredient state
- Step completion state
- Visual scene understanding
- Timers and cooking context

The result should feel closer to having an **AI cooking partner beside you** than reading a recipe page.

### 6. Shared Food Spaces

Inventory should belong to a **space**, not only one user.

Examples:

- Couple / partners
- Bachelor friends sharing a flat
- Family
- Hostel/PG roommates
- Small restaurants
- Hotels
- Kitchens and catering teams

Members can have roles and permissions, while all permitted members see a synchronized inventory and activity history.

---

## Example End-to-End Journey

A user opens CAM2COOK and points the camera at their refrigerator.

### Step 1 — Scan

Computer vision identifies:

```text
Tomatoes
Chicken
Spinach
Onions
Eggs
Milk
Cheese
```

The user confirms the detections and adjusts quantities if necessary.

### Step 2 — Inventory

The backend stores each item with metadata such as location, quantity, capture date, and freshness/deadline information.

### Step 3 — Reminder

Later, the system determines that chicken should be used soon and sends a notification.

### Step 4 — Decision

The user opens the notification:

> **Chicken should be used today. What would you like to cook?**

CAM2COOK proposes recipes that maximize the use of ingredients already available.

### Step 5 — Recipe Gap Analysis

For the selected recipe, the system answers:

```text
✓ Chicken
✓ Onion
✓ Garlic
✓ Capsicum
✓ Soy Sauce
✗ Sesame Oil
```

The user can replace the missing ingredient with an available alternative where appropriate, add the item to a shopping list, or choose another recipe.

### Step 6 — Cook Mode

The user places the phone where the camera can see the counter and stove.

CAM2COOK starts a structured cooking session:

```text
1. Prepare ingredients
2. Wash
3. Chop
4. Heat pan
5. Add oil
6. Add aromatics
7. Add chicken
8. Stir / cook
9. Add sauce
10. Finish
11. Plate
```

The AI tracks the current state and adapts its guidance based on what it sees and what the user says.

### Step 7 — Human-like Assistance

The assistant can proactively guide the user:

> “Your pan is hot enough. Add the chicken now.”
>
> “The chicken pieces on the left are still undercooked. Keep them on the heat for another minute.”
>
> “You have completed this step. Reduce the heat before adding the sauce.”

### Step 8 — Completion

When the recipe is finished, CAM2COOK records the consumed ingredients, updates inventory quantities, and can recommend what to cook next based on the remaining food.

---

## Core Product Loop

```text
             ┌───────────────────────┐
             │   CAMERA / USER INPUT │
             │   Photo • Video • Voice│
             └───────────┬───────────┘
                         │
                         ▼
             ┌───────────────────────┐
             │   VISION + AI LAYER   │
             │ Detection • OCR • VLM  │
             └───────────┬───────────┘
                         │
                         ▼
             ┌───────────────────────┐
             │   FOOD INVENTORY      │
             │ Item • Qty • Location │
             │ Deadline • Confidence │
             └───────────┬───────────┘
                         │
             ┌───────────┴───────────┐
             │                       │
             ▼                       ▼
   ┌──────────────────┐    ┌──────────────────┐
   │ EXPIRY / FRESHNESS│    │ RECIPE ENGINE    │
   │ MONITORING        │    │ + SHOPPING GAP   │
   └────────┬─────────┘    └────────┬─────────┘
            │                       │
            └───────────┬───────────┘
                        ▼
             ┌───────────────────────┐
             │   COOK MODE / AGENT   │
             │ Vision + Voice + State│
             └───────────┬───────────┘
                         │
                         ▼
             ┌───────────────────────┐
             │ CONSUMPTION UPDATE    │
             │ Inventory + History   │
             └───────────────────────┘
```

---

## High-Level Architecture

CAM2COOK is designed as a mobile + web application backed by AI services and an event-driven backend.

### Client Applications

- **Mobile app** — primary camera, notifications, inventory, recipe, and Cook Mode experience
- **Web app** — inventory management, household/team dashboards, hotel/restaurant operations, recipes, analytics, and administration

### API / Application Layer

Responsible for authentication, users, households/workspaces, inventory, recipes, shopping lists, notifications, cooking sessions, permissions, and orchestration.

### AI / Computer Vision Layer

Potential components include:

- Object detection / segmentation
- Food classification
- OCR / barcode / label extraction
- Vision-language models
- Image/video frame understanding
- Hand/object interaction detection
- Cooking state recognition
- Audio speech-to-text
- Text-to-speech
- Recipe reasoning / generation

AI outputs should always be treated as probabilistic and should expose confidence/uncertainty where it matters.

### Data Layer

A production architecture can separate concerns across:

- **Relational database** for users, workspaces, inventory, recipes, sessions, permissions, and events
- **Object storage** for uploaded photos/videos and optional cooking-session media
- **Cache** for hot inventory views, session state, rate limits, and frequently requested data
- **Search/vector layer** for semantic recipe retrieval and future personalization
- **Queue/event bus** for notifications, asynchronous vision processing, recipe jobs, analytics, and retries

### Background Workers

Useful asynchronous jobs include:

```text
Media ingestion
Vision inference
Freshness estimation
Inventory reconciliation
Deadline monitoring
Notification scheduling
Recipe indexing
Recommendation generation
Analytics aggregation
Cleanup / retention jobs
```

### Real-Time Cooking Runtime

Cook Mode needs a lower-latency path than normal inventory workflows.

A session can maintain:

```text
SESSION_CREATED
→ CALIBRATING
→ INGREDIENT_CHECK
→ STEP_ACTIVE
→ OBSERVING
→ GUIDANCE_READY
→ USER_CONFIRMED
→ STEP_COMPLETED
→ NEXT_STEP
→ RECIPE_COMPLETED
```

The runtime should combine recipe state with observations rather than asking a general-purpose model to solve the entire task from scratch on every frame.

---

## Safety & Trust Principles

Food safety is a core product requirement.

CAM2COOK should **not** present an image-based freshness estimate as a guaranteed indication that food is safe to eat. The system should clearly distinguish:

- Known labeled date
- User-entered date
- Estimated use window
- Uncertain / review-required state

The product should encourage users to follow package storage instructions and applicable food-safety guidance. Camera-based visual inspection can assist decision-making but cannot reliably detect every microbiological hazard.

Cook Mode should also avoid overclaiming capabilities. For example, camera-only temperature estimation should be presented as an estimate unless a calibrated external sensor is available.

---

## Target Users

### Individuals

- Students and bachelors
- Busy professionals
- First-time / beginner cooks
- People trying to reduce food waste
- People who want cooking assistance at home

### Shared Households

- Couples
- Partners
- Families
- Roommates / bachelor groups
- Parent-child cooking workflows

### Hospitality & Food Operations

- Hotels
- Hostels / PGs
- Small restaurants
- Cafes
- Shared kitchens
- Catering / meal-prep operations

For business users, future capabilities can include role-based access, stock alerts, kitchen-level inventory, waste analytics, and multi-user operational dashboards.

---

## Feature Roadmap

### Phase 1 — Foundation

- Landing page and product prototype
- User accounts
- Photo-based ingredient capture
- Manual inventory correction
- Inventory CRUD
- Basic deadline tracking
- Notifications

### Phase 2 — Visual Inventory

- Object detection / segmentation
- OCR and barcode support
- Multi-item image understanding
- Quantity estimation
- Storage-location tracking
- Confidence and correction workflow

### Phase 3 — Recipe Intelligence

- Inventory-aware recipe search
- “Use this first” recommendations
- Missing ingredient detection
- Ingredient substitutions
- Shopping list generation
- User preference learning

### Phase 4 — Shared Spaces

- Household/workspace model
- Invites
- Roles and permissions
- Shared inventory
- Activity history
- Hotel/kitchen dashboards

### Phase 5 — Real-Time Cook Mode

- Live camera session
- Voice conversation
- Step-aware agent
- Visual progress detection
- Timers
- Contextual feedback
- Recovery from mistakes / skipped steps
- Cooking-session summaries

### Phase 6 — Advanced Intelligence

- Personalized recipe ranking
- Food waste analytics
- Multi-camera / device workflows
- Optional external sensor integrations
- Smarter preservation recommendations
- Proactive meal planning
- Business forecasting and procurement support

---

## Current Repository State

The repository currently contains a **frontend concept / landing-page prototype**. The existing implementation already communicates the scan → decide → guide experience through animated scenes, feature sections, an interactive camera-feed simulation, and theme support. The current JavaScript includes the hero scene loop, simulated camera guidance, theme switching, navigation behavior, and demo interactions. fileciteturn1file0 fileciteturn3file0

The current landing-page copy already positions CAM2COOK around camera-based ingredient scanning, recipe selection, and live cooking guidance, which aligns with the expanded product direction. fileciteturn4file0

### Current frontend structure

```text
frontend/
└── src/
    ├── index.html
    ├── demo.html
    ├── early-access.html
    ├── video.html
    ├── privacy.html
    ├── terms.html
    ├── contact.html
    ├── css/
    │   └── styles.css
    └── js/
        └── script.js
```

### Local prototype

From the repository root:

```bash
python -m http.server 8000
```

Open:

```text
http://localhost:8000/frontend/src/
```

### Current technology

The current prototype uses:

- HTML5
- CSS3
- Vanilla JavaScript

The README previously described the project as a frontend-only prototype with AI/backend integration planned for future development; this document now treats that prototype as the foundation for the full CAM2COOK platform. fileciteturn1file0

---

## Suggested Future Repository Structure

As the product moves from prototype to system, a scalable structure could evolve toward:

```text
cam2cook/
├── frontend/
│   ├── web/
│   └── mobile/
├── backend/
│   ├── api/
│   ├── workers/
│   ├── realtime/
│   └── notifications/
├── ai/
│   ├── vision/
│   ├── recipe/
│   ├── cooking-agent/
│   └── evaluation/
├── data/
│   ├── schemas/
│   ├── seed/
│   └── migrations/
├── infrastructure/
│   ├── docker/
│   ├── deployment/
│   └── monitoring/
├── docs/
│   ├── architecture/
│   ├── api/
│   └── decisions/
├── README.md
└── SRS.md
```

---

## Why CAM2COOK Is Different

Most recipe applications start from a recipe and ask the user to buy or gather ingredients.

CAM2COOK starts from the **physical food already around the user**.

Most inventory applications stop after telling the user that an item exists or expires soon.

CAM2COOK turns that information into a next action:

```text
“I have food.”
      ↓
“What needs attention?”
      ↓
“What should I make?”
      ↓
“What am I missing?”
      ↓
“How do I cook it?”
      ↓
“Is what I'm doing correct?”
      ↓
“I'm done.”
```

That continuity—from **camera capture to food consumption**—is the core product thesis.

---

## Status

**Current:** Frontend concept / landing-page prototype  
**Goal:** Production-ready multimodal food lifecycle + AI cooking platform

See [`SRS.md`](SRS.md) for the detailed software requirements specification and system requirements.
