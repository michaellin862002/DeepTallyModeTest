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


## v1.0-beta8
- Replaces History with a Month analytics tab.
- Week and Month periods can be selected by year.
- Horizontal swipe moves to the previous/next week or month; pull down at the top returns to the current period.
- Current periods remain clearly labeled This week / This month.
- Month bars use complete Monday–Sunday weeks, including adjacent-month days when needed.
- Month total still counts only dates inside the selected calendar month.
- Month drill-down: week → day → session.
- Duration formatting below top totals is standardized to HH:MM.


## v1.0-beta9
- Week chart redesigned as seven horizontal day rows.
- Week day rows use aligned HH:MM, weekday, date, and bar columns; weekday/date remain visually grouped.
- Weekly goal status is shown with an outlined/filled star next to the top total.
- Re-entering Week from the bottom tab always returns to This week; History drill-down remains an intentional exception.
- Month is replaced by History.
- History shows weekly horizontal bars from newest to oldest, a weekly-goal reference line, month separators, and calendar-month totals.
- History can filter by year; tapping a week jumps to that exact week in Week.


## v1.0-beta10
- History title now shows the current year explicitly: This year · YYYY.
- History begins at the first Monday-grouped month containing a counted deep-work week, then shows every week continuously through the current week (or year end for past years), including 00:00 weeks and completely empty later months.
- Month summaries use a compact one-line format such as Sep 12 hrs 38 mins.
- History uses narrower page margins and wider chart content.


## v1.0-beta11
- History month summaries use two lines: the month on the first line and "X hrs X mins" on the second.


## v1.0-beta12
- History left inset is slightly wider for breathing room.
- The month-summary column is shifted right and the right padding is reduced.
- Weekly bars gain more horizontal drawing space without changing the data scale.
