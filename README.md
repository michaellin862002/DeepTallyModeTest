# Deep Tally Mode Test

A tiny Android test app for checking whether a Samsung **Mode** configured with the condition **App opened**:

1. Turns on when this app is opened.
2. Keeps running after this app leaves the foreground.
3. Applies the expected Do Not Disturb (DND) state.

## Test steps

1. Build/install the APK.
2. On the Samsung phone, open:
   **Settings → Modes and Routines → Deep Work → Turn on automatically → App opened**
3. Select **Deep Tally Mode Test**.
4. Open the test app and check whether the Samsung Deep Work mode turns on.
5. Tap **OPEN SETTINGS — LEAVE APP** or **GO HOME — LEAVE APP**.
6. Without returning to the test app, check the status bar / Quick Settings:
   - If Deep Work / DND stays on, the Samsung automation may be usable for Deep Tally.
   - If it immediately turns off, the final app should not rely on this trigger alone.

The app itself does **not** change DND. It only reads the current interruption-filter state and gives you buttons for leaving the app during the test.
