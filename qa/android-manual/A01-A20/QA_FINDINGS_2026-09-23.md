# QA Findings After Android Manual Acceptance — 2026-09-23

## Purpose

This document reconciles the manual observations from the A01–A20 session with the current repository implementation and project documentation.

## Finding 1 — Vertical camera rotation / pitch

**Classification: Potential defect — requires retest**

Manual observation:
- Horizontal camera rotation around the vertical axis works.
- The user reported that rotation around the horizontal axis did not work.

Repository evidence:
- `project/PROJECT_STATE.md` states that the Android prototype camera supports horizontal drag for yaw and vertical drag for pitch within bounded limits.
- `AdventureRenderer.kt` implements:
  - yaw from `dx`
  - pitch from `dy`, bounded to `0.15f..1.25f`.

Conclusion:
The documented/implemented behavior says vertical drag should affect camera pitch, but the manual test did not observe it. This should be retested on the current APK with deliberate large upward/downward swipes. If still not observable, create a bug against camera pitch.

**Important correction:** A01 was recorded as PASS during the live session, but the vertical-pitch observation means A01 should be treated as **PASS for yaw / NEEDS RETEST for pitch**, not as a clean full PASS.

## Finding 2 — Landscape orientation

**Classification: Expected behavior**

The Android manifest explicitly sets:

`android:screenOrientation="landscape"`

Therefore the observed behavior that device rotation does not switch the game to portrait is intentional in the current prototype.

No bug.

## Finding 3 — Pinch-to-zoom

**Classification: Not a defect / requirement not established**

The manual test found that pinch does not zoom. The current camera implementation has no zoom/pinch-distance handling; touch movement is used for yaw/pitch.

The approved project context requires a rotatable camera, but no repository evidence was found requiring pinch-to-zoom.

No bug should be created from this observation.

## Finding 4 — Two-finger gesture rotates camera

**Classification: Implementation observation**

The renderer currently reads `event.x/event.y` from the MotionEvent without separate pointer handling. Therefore multi-touch movement can be interpreted as camera rotation rather than a dedicated pinch gesture.

This is consistent with the current prototype implementation and is not currently a confirmed product defect.

## Finding 5 — Quest completion message

**Classification: Expected progression behavior**

The observed message:

- Деревенская площадь
- Квест завершен
- +75 ХР, +15 монет
- Новый путь открыт

is consistent with the repository's quest → game progression design, where quest completion produces progression rewards and unlocks.

The message disappearing after a full restart does not by itself indicate reward loss; the current manual session did not verify the persisted XP/coin values independently.

No bug should be created from the message alone.

## Current QA disposition

| Finding | Disposition |
|---|---|
| Camera yaw | PASS |
| Camera pitch | RETEST REQUIRED |
| Landscape lock | EXPECTED |
| Pinch zoom absent | NO DEFECT CONFIRMED |
| Two-finger rotation | OBSERVATION |
| Quest completion message | EXPECTED / persistence of numeric reward not independently verified |

## Next action

Before changing product code, perform one focused camera-pitch retest on the current APK:

1. Start at Home.
2. Make a very large slow swipe from bottom to top.
3. Repeat top to bottom.
4. Compare the visible horizon/roof/upper surfaces.
5. If no visible pitch change occurs, record a camera-pitch defect and then inspect the implementation path for the fix.

No code changes are recommended until this retest is completed.
