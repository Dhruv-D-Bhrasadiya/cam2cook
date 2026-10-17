# Cam2Cook Android

Native Kotlin Android client for CAM2COOK, built with Jetpack Compose and Material 3.

## Implemented in this slice

- Figma-inspired welcome, login and sign-up screens
- Home dashboard with deadline attention card, quick actions and inventory summary
- Searchable inventory with freshness filters, quantity editing and mark-as-used action
- Scan-to-review flow with editable selection of sample detections
- Recipe search/filter cards and step-by-step Cook Mode with step progress and timer presets
- Safety copy that distinguishes freshness estimates from verified package dates

> **Integration status:** Auth currently performs client-side validation and enters a local demo session. The scan and AI-recognition screens use sample detections. API authentication, live CameraX capture, real vision inference, remote recipe generation, persistent inventory and an actually ticking timer are not wired yet.

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

The backend currently documents health and authentication routes. Set up the backend per [`../../backend/docs/api/README.md`](../../backend/docs/api/README.md), then connect the client repository to:

- `POST /api/v1/auth/register` — JSON name, email and password
- `POST /api/v1/auth/login` — URL-encoded username (email) and password, returns a bearer token
- `GET /api/v1/auth/me` — bearer token required

The manifest currently allows cleartext traffic to support local development. Remove that allowance for production and use HTTPS.

## Design

Main reference: [Cam2Cook Android Figma file](https://www.figma.com/design/0liuRYK0NH9Xjfvzkf738h/Cam2Cook?node-id=0-1). Tokens: ink background `#090E17`, slate cards `#172138`, red CTA `#FF0040`, teal accent `#4ECDC4`, muted text `#CBD5E0`.
