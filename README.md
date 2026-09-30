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
This is the canonical beta4 and is based directly on **v1.0-beta3**.

The only automation change is that Deep Tally sends Android broadcast intents for MacroDroid:

- START: `com.deeptally.DEEP_WORK_START`
- STOP: `com.deeptally.DEEP_WORK_END`
- UNDO after stopping: sends START again

The Samsung Mode ID is intentionally not stored in Deep Tally. MacroDroid remains responsible for device-specific Samsung Mode control.

The older experimental strong-haptic beta4 is retired; beta4 keeps beta3's light haptic behavior.
