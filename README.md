# Deep Tally

Minimal Android deep-work tally. **Plan elsewhere. Tally here.**

## Core
- One large START/STOP circle.
- Wall-clock timer survives backgrounding/process death.
- Sessions count only at >= 30:00; shorter attempts are kept but struck through.
- Week bars + weekly goal.
- History by date.
- 我的自傳 with edit/save and autosaved drafts.
- Local-only SQLite + SharedPreferences.
- Samsung Routine triggers: `START_DEEP_WORK` and `STOP_DEEP_WORK`, silent and auto-expiring.

Keep the two Samsung routines already created:
1. START_DEEP_WORK -> 開啟模式 -> 深度工作
2. STOP_DEEP_WORK -> 詢問 Bixby -> 關閉深度工作模式
