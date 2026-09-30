# Deep Tally

Minimal Android deep-work tally. **Plan elsewhere. Tally here.**

## Core
- One large START/STOP circle with the **beta3 light click haptic** preserved.
- Wall-clock timer survives backgrounding/process death.
- Sessions < 10:00 are discarded. Sessions 10:00–29:59 are kept but struck through. Sessions >= 30:00 count toward totals.
- Week bars + weekly goal.
- History by date.
- 我的自傳 with edit/save and autosaved drafts.
- Local-only SQLite + SharedPreferences.

## v1.0-beta4
Canonical beta4 is based directly on v1.0-beta3 and adds MacroDroid integration:
- START: `com.deeptally.DEEP_WORK_START`
- STOP: `com.deeptally.DEEP_WORK_END`
- UNDO after stopping: sends START again

The Samsung Mode ID stays outside the app and is handled by MacroDroid.

## v1.0-beta5
Adds data protection for long-term use:
- Settings now includes **匯出備份** and **還原備份**.
- Backups are portable JSON files created through Android's Storage Access Framework.
- The backup contains all saved sessions, weekly goal, biography text, drafts, and biography update timestamp.
- Backup/restore is blocked while a timer is running.
- Restore validates the file before replacing local data.
- A permanent release-signing workflow is prepared. The release keystore itself is never committed to this public repository.

### Signing
GitHub Actions always compiles a debug APK for CI verification. A permanently signed release APK is built only when these repository secrets exist:
- `DEEP_TALLY_KEYSTORE_B64`
- `DEEP_TALLY_KEYSTORE_PASSWORD`

The key alias is fixed to `deeptally`. Keep the private keystore and password outside the repository.
