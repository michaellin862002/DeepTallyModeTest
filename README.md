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


## v1.0-beta14
- Adds manual session entry from Today and the selected day in Week.
- Manual entry supports overnight sessions, blocks future/end-equal-start/under-10-minute entries, warns for 10–29-minute sessions, and prevents overlaps.
- Editing also prevents overlapping sessions and invalid/future times.
- Cross-midnight sessions remain one database row but are split at real day/week/month boundaries for statistics and day detail display.
- Week and History left-side HH:MM values use bold dark green to match the chart palette.
- History Goal marker now shows the configured HH:MM directly below Goal.


## v1.0-beta15
- Hotfix: fixes a History crash caused by week dates not being assigned after the beta14 boundary-aware statistics refactor.


## v1.0-beta16
- History again follows the first-recorded-month rule: determine the first week that contains counted Deep Work, use that week's Monday month as the first month, and display every Monday-start week in that month even when some weeks are 00:00.
- All later weeks continue to display without gaps.


## v1.0-beta17
- Fixes Week chart paint-state leakage: only the left HH:MM duration is dark green/bold; weekday and date return to dark gray.
- Restores newest-first ordering for session rows while keeping boundary-aware statistics.
- History year selector now lists only years containing counted Deep Work, plus the current year.
- Re-audited beta14–beta16 behavior against the agreed specification.


## v1.0-beta18
- Highlights the current week row in History with a subtle pale green rounded background.
- The highlight covers only the weekly data area (time, date, and bar), stopping before the month-summary column.
- Goal line, labels, bars, and month summaries remain drawn above/alongside the highlight.


## v1.0-beta19
- Week daily bars now use one consistent scale across all weeks in the same year instead of rescaling to each week's maximum day.
- The scale is the year's highest counted single-day Deep Work total, rounded up to the next whole hour, with a minimum full-scale value of 4 hours.
- The current year's scale only considers dates through today; it increases only when a new higher single-day record is reached.
- No extra scale label is shown; exact HH:MM values remain the primary numeric reference.


## v1.0-beta20
- Week daily-bar scale is recalculated from the current data whenever Week is drawn, so editing or deleting the previous highest day can lower the scale again.
- Each year's scale remains: max(4 hours, that year's highest counted single-day Deep Work rounded up to the next whole hour).
- A week that spans two calendar years uses the larger of those two yearly scales for all seven bars, preventing either side of the year boundary from exceeding the chart scale.
- Week numbering and the Week picker continue to use ISO week-year rules (the Thursday/4-day rule).
- History grouping remains Monday-based; no History scale behavior was changed in this release.


## v1.0-beta21
- Adds two secondary Today timer controls while keeping the large central START/STOP circle as the primary action.
- `Start from…` is available only while idle. It lets the user choose a past date/time, starts the live timer from that timestamp, blocks overlap with existing sessions, and sends the Deep Work START broadcast when confirmed.
- `Stop at…` is available only while timing. It lets the user choose a past end date/time, saves the session using that corrected endpoint, applies the existing <10 / 10–29 / >=30 minute rules, and sends the Deep Work END broadcast when confirmed.
- Both controls support cross-midnight corrections through date + time pickers.
- Active auxiliary controls use the existing pale-green / dark-green Deep Tally palette; inactive controls remain visible in a subdued gray state and do not respond.
- Existing STOP/UNDO behavior is preserved, including restoring the running timer if an adjusted stop is undone.


## v1.0-beta22
- Redesigns the Today auxiliary timer controls to match the original hand-drawn “rotated Mickey ears” concept.
- The large central START/STOP circle keeps its original size and remains the dominant control; the cluster is only shifted slightly left to make room for the two independent ear buttons.
- Each auxiliary control is now an 88 dp near-circle with a concave cut-out whose curvature follows the main timer circle, plus a small visual gap so it reads as a separate button.
- Active auxiliary controls use deep ink green (#3D6A5C) with white two-line text; inactive controls keep the beta21 pale-gray / gray palette.
- Transparent cut-out areas are excluded from the auxiliary button hit target so taps on the main circle are not intercepted.
- Start/stop correction behavior is unchanged from beta21.


## v1.0-beta23
- Restores the main START/STOP circle to the exact horizontal center of Today and keeps its 276 dp size unchanged.
- Moves the two auxiliary correction controls above the main circle in a symmetric upright “Mickey ears” layout: Start from… at upper-left and Stop at… at upper-right.
- The 88 dp ear buttons keep the beta22 concave cut-out and small separation gap; the cut-outs are mirrored toward the central circle.
- Active auxiliary controls are softened one shade to muted green (#557A6E) with white text, while inactive controls keep the existing pale-gray / gray styling.
- All Start from… / Stop at… behavior remains unchanged from beta22.


## v1.0-beta24
- Reduces accidental taps between the auxiliary correction buttons and the main START/STOP circle without changing the beta23 visual hierarchy.
- Increases the visible gap around the notched ear buttons from about 5 dp to about 10 dp.
- Keeps each auxiliary button visually 88 dp, but enlarges its invisible touch target to about 104 dp; the extra hit area expands outward while remaining excluded near the main circle.
- The main timer now accepts taps only inside its actual circular face instead of its full square View bounds.
- Adds small invisible no-tap safety zones on the main circle beside the two ear buttons, so near-miss taps are ignored instead of accidentally starting or stopping a session.
- Start from… / Stop at… behavior, colors, positions, and the centered 276 dp main circle remain otherwise unchanged.


## v1.0-beta25
- Tightens the visible gap between each auxiliary ear button and the main timer from about 10 dp to about 8 dp for a more cohesive Mickey-ear composition.
- Keeps the beta24 enlarged auxiliary touch target and its wider near-main exclusion unchanged, so the anti-mistap protection remains stronger than the visible gap suggests.
- Keeps the main circle's true circular hit area and the invisible safety zones beside both ear buttons unchanged.
- No timer behavior, colors, sizes, positions, or session logic changed.


## v1.0-beta26
- Replaces the two auxiliary correction buttons with one centered secondary button above the main timer.
- The secondary button is visually larger than the previous ears (about 104 dp visible diameter) and keeps the muted active green (#557A6E) with white two-line text.
- While idle it shows Start / from… and uses the existing Start from… workflow; while timing it changes to Stop / at… and uses the existing Stop at… workflow.
- The secondary button has a concave lower cut-out following the main timer's curvature and keeps an 8 dp visual gap.
- Its invisible touch target is slightly larger than the visible button but is clipped away from the gap and main timer.
- The main timer remains horizontally centered and its clickable area is strictly its circular painted face, never the surrounding square View.
- The main timer's anti-mistap zone is reduced to a small 18 dp safety crescent directly below the secondary button so intentional taps near the top of the main circle still work normally.
- All retrospective start/stop rules, overlap checks, broadcasts, thresholds, undo behavior, and beta3 light haptic remain unchanged.
