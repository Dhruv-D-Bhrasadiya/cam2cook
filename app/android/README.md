# Cam2Cook Android

Native Kotlin Android client for CAM2COOK, built with Jetpack Compose and Material 3. The Android design targets Samsung Galaxy phones and adapts the iOS source design to Android/One UI interaction and system UI.

## Design direction

- Reference: [CAM2COOK iOS source design](https://www.figma.com/design/QjeivGZjLYvgU9kVV34FvK/Cam2Cook2?node-id=121-173)
- Target: Samsung Galaxy S-series portrait layouts (design reference 360 × 800 dp; Compose layouts remain responsive)
- Palette: canvas `#F2F2F7`, white surfaces `#FFFFFF`, forest green `#0B6B4F`, mint `#D1F2DE`, secondary text `#5A6861`
- Android-specific details: native status/navigation bars, system insets, touch targets, Material text fields and buttons. iPhone-specific Dynamic Island/home indicator treatments are not used.

## Implemented

- Welcome/onboarding screen aligned to the source design's fridge illustration, green palette, copy, and primary/secondary actions
- Home dashboard with the green scan hero, “Use these first” food rows, recipe recommendation, and five-destination One UI-style bottom navigation
- Searchable inventory with freshness filters, quantity editing and mark-as-used action
- Scan-to-review flow with editable sample detections
- Recipe search/filter cards and step-by-step Cook Mode with progress and timer presets
- Safety copy distinguishes freshness estimates from verified package dates

> **Integration status:** Auth currently performs client-side validation and enters a local demo session. Scan/AI recognition uses sample detections. API authentication, live CameraX capture, real vision inference, remote recipe generation and persistent inventory are not connected.

## Build

Requirements: Android Studio with JDK 17 and Android SDK platform 35.

1. Open `app/android` in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an Android emulator or device (Android 8.0 / API 26+).

Command-line build from this folder:

```bash
gradle assembleDebug
```

An installed Gradle distribution is required; a Gradle wrapper has not been checked in yet.

## Backend

The backend currently documents health and authentication routes. Set up the backend per [`../../backend/docs/api/README.md`](../../backend/docs/api/README.md), then connect the client to:

- `POST /api/v1/auth/register` — JSON name, email and password
- `POST /api/v1/auth/login` — URL-encoded username (email) and password, returns a bearer token
- `GET /api/v1/auth/me` — bearer token required

The manifest currently allows cleartext traffic for local development. Remove that allowance for production and use HTTPS.
