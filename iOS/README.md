# CAM2COOK for iOS

Native SwiftUI iOS client prototype for the CAM2COOK food inventory and freshness-tracking experience. This branch keeps the existing web/backend/model work intact.

## Open in Xcode

1. Open `iOS/Cam2Cook.xcodeproj` in Xcode 15 or newer.
2. Select the `Cam2Cook` scheme and an iPhone simulator running iOS 17+.
3. Build and run.

## Included

- Native dark-mode layout with iOS tab navigation.
- Home dashboard, tracked-food metrics, freshness reminders and recipe inspiration.
- Inventory search and filters, add item form, and mark-consumed action.
- Photo picker scan flow with an explicit demo-mode notice; AI inference is not falsely represented as connected.
- Recipe sample cards and profile/preferences screen.
- GitHub Actions workflow builds an iOS Simulator app and uploads it as a downloadable artifact.

## Download a build

Open **Actions → Build Cam2Cook iOS app → latest successful run → Artifacts** in GitHub. Download `Cam2Cook-iOS-Simulator-<run>`. This artifact is for the iOS Simulator, not direct installation on a physical iPhone.

A signed iPhone install/TestFlight build requires an Apple Developer team, registered bundle identifier, signing certificate and provisioning profile, plus App Store Connect credentials. Those are intentionally not fabricated or committed to the repository. After configuring those credentials, a separate signed archive/TestFlight release workflow can be enabled.

## Current integration boundary

The app currently uses sample inventory and recipe data in memory. AI food recognition, backend authentication/sync, push notifications, and real recipe APIs are not connected yet. The scan UI uses the iOS photo picker but clearly labels recognition as demo-only. Freshness values are estimates and must not be treated as food-safety guarantees.
