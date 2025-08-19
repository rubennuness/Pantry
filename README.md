# Smart Grocery + Pantry (Android)

MVP features:
- Receipt scan (stub for MVP; add sample items)
- Expiring items view
- 5-day meal plan generated from what’s expiring
- One-tap store list (dynamic by waste risk)

## Build & Run (Android Studio with JBR 21)
1. Open Android Studio (Hedgehog+).
2. Ensure JBR 21 is used: Android Studio ships with JBR 21; no action needed. If you changed JDK, set it back under `File > Settings > Build, Execution, Deployment > Build Tools > Gradle` -> Gradle JDK: `Embedded JDK`.
3. Open this folder (`/Users/rubennunes/Library/CloudStorage/OneDrive-Personal/Pantry`).
4. Let Gradle sync. If prompted to upgrade Kotlin/AGP, keep versions in `gradle/libs.versions.toml`.
5. Select an emulator (API 34 recommended) or a device.
6. Click Run ▶ on the `app` configuration.

Notes:
- Min SDK 24, target SDK 34.
- Kotlin 2.0, Compose M3, Navigation Compose.
- For now, data is in-memory with sample items; Room/OCR can be added later.

## Product search (SerpAPI)
To enable real product search across PT supermarkets via Google results:
1. Get a SerpAPI key.
2. Add to `local.properties` (do not commit):
   SERPAPI_KEY=your_key_here
3. Sync Gradle and Run. If the key is blank, the app falls back to mock results.