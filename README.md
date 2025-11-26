## Automation framework overview

I have added a Jetpack Compose UI automation layer built with Kotlin, Hilt, and POM. Tests run as `connectedAndroidTest` and use test tags/content descriptions for stable selectors.

### Project changes
- Fixed instrumentation runner typo `com.test.muzz.CustomTestRunner`.
- Added a UI Automator dependency for any device-level commands.
- Added stable test tags to login inputs/button/error, existing tags in Profiles were reused.
- Tests consume credentials from instrumentation args (defaults set in Gradle).
  - These inline defaults are for the take home test only, in a real project you would keep creds out of the repo (gitignored locally) or inject via CI secrets/env vars.

### Test helpers
- `BaseTest.kt`: shared Hilt + Compose rules.
- `TestCredentials.kt`: reads `testUser`/`testPassword` from instrumentation args.
- Page Objects:
    - `LoginPage.kt`: wraps username/password entry, login action, error assertion.
    - `ProfilesPage.kt`: wraps loading wait, liking/passing actions, error/retry assertions.

### Tests
- `LoginTests.kt`
    - Wrong creds show error.
    - Valid creds navigate to Profiles.
    - Recreate activity after login keeps you on Profiles.
    - “New session” recreation also keeps you on Profiles (closest I could get without persisted login state).
- `ProfilesTests.kt`
    - Profiles load after login (when online).
    - Deck finishes after likes/passes on all profiles.
- `ProfilesErrorTests.kt`
    - Overrides the profile repository via Hilt (`@UninstallModules`) to always throw, asserting the “Failed to profiles” error UI and retry button. This simulates “no internet” since the app uses a fake repo and has no real network path.

### Running tests
```
./gradlew connectedAndroidTest
```
Instrumentation args for creds are set in `app/build.gradle.kts` under `testInstrumentationRunnerArguments`.
### Notes on Scenario 4 (returning users)

**Bug (manual): Logged-in users are not kept on cold start**
- Environment: emulator/device, any locale.
- Steps:
    1) Launch app, enter valid creds (`user`/`password`), land on Profiles.
    2) Force-stop or fully close the app.
    3) Relaunch from launcher.
- Expected: App should detect prior login and go straight to Profiles.
- Actual: App always shows the login screen again.
- Impact: Scenario 4 fails; returning users must log in each time.
- Potential fix: Introduce persisted/injectable session state (e.g., `SessionManager` backed by DataStore/SharedPreferences) and choose NavHost start destination based on stored login flag/token; set/reset on login/logout.
