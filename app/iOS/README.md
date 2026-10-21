# CAM2COOK for iOS

Native SwiftUI iOS client prototype for CAM2COOK.

## Open in Xcode
1. Open `app/iOS/Cam2Cook.xcodeproj` in Xcode 15 or newer.
2. Select the `Cam2Cook` scheme and an iPhone simulator running iOS 17+.
3. Build and run.

## Included
- Native dark-mode UI with iOS tab navigation.
- Home dashboard, inventory metrics, freshness reminders and recipe inspiration.
- Inventory search and filters, add item form, and mark-consumed action.
- Photo picker scan flow with a clear demo-mode notice.
- Sample recipe cards and profile/preferences screen.
- GitHub Actions builds and uploads a downloadable iOS Simulator artifact.

## Download a build
Open **Actions → Build Cam2Cook iOS app → latest successful run → Artifacts**. Download `Cam2Cook-iOS-Simulator-<run>`. This artifact is for the iOS Simulator, not direct installation on a physical iPhone.

## Integration status
Inventory and recipes currently use in-memory/sample data. AI food recognition, backend authentication/sync, push notifications and real recipe APIs are not yet integrated. Freshness values are estimates, not food-safety guarantees.

A signed iPhone/TestFlight build requires an Apple Developer team, signing certificate and provisioning profile, plus App Store Connect configuration.
