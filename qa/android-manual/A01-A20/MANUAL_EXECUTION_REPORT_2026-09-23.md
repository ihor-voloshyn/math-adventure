# Manual Android Acceptance Execution Report — 2026-09-23

## Scope

This report records the **actual manual exploratory acceptance run performed in chat on 2026-09-23** against the Android debug APK built from commit `d3bf8bd9ac2ea3ad47360c1e79245311520e21db`.

Important: the original A01–A20 files in this PR were prepared earlier with a different functional scope. During the live manual session, the A01–A20 sequence was adapted to the behavior available in the installed prototype. Therefore, this report records the **tests actually executed**, and it does not claim that the original pre-written functional scenarios (for example, combat/math/equipment scenarios) were executed.

## Overall result

**20 / 20 executed exploratory checks: PASS**

No crash, freeze, black screen, or unrecoverable application state was observed during the run.

## Results

| ID | Executed check | Result | Notes |
|---|---|---|---|
| A01 | Camera rotation around vertical axis | PASS | Horizontal/vertical-axis behavior and zoom were observed separately; vertical-axis rotation works. |
| A02 | Cold relaunch and initial scene | PASS | Green field, blue sky, abstract house/object loaded successfully. |
| A03 | Scene stability under repeated camera interaction | PASS | Repeated rotation and idle period produced no crash/freeze/disappearance. |
| A04 | Full app close and relaunch | PASS | App relaunched correctly and scene remained usable. |
| A05 | Android Back and return to app | PASS | Back moved the app to background; reopening worked normally. |
| A06 | Device orientation change | PASS | Game remains locked to landscape; rotating the device does not rotate the game. |
| A07 | Screen lock/unlock recovery | PASS | Scene and camera remained usable after screen off/on. |
| A08 | Multitouch stability | PASS | Two-finger input did not crash or break the scene. |
| A09 | Rapid taps and swipes | PASS | Rapid input did not crash/freeze the app. |
| A10 | Repeated Back / app lifecycle | PASS | Repeated Back handling remained stable. |
| A11 | Switch to another app and return | PASS | App returned correctly and camera remained usable. |
| A12 | 5-minute idle stability | PASS | Scene remained responsive after prolonged idle. |
| A13 | Camera control after idle | PASS | Camera still responded after idle period. |
| A14 | Extended rotation in one direction | PASS | 20–30 repeated swipes per direction caused no invalid camera state. |
| A15 | Rapid alternating camera input | PASS | Camera remained functional. Quest-completion text appeared and remained during the session. |
| A16 | State after full relaunch | PASS | After full relaunch, the quest-completion message disappeared and "Дом героя / Питомец ждет нового приключения." appeared. |
| A17 | Start-state interaction | PASS | Camera interaction from "Дом героя" remained functional. |
| A18 | Relaunch and interaction / multitouch observation | PASS | Relaunch worked. One-finger tap did nothing; two-finger tap and pinch caused camera/object rotation; no zoom observed. |
| A19 | System interruption / notification shade | PASS | Returning from Android notification shade left the app functional. |
| A20 | Final smoke test | PASS | Relaunch, camera, taps, multitouch, notification shade and Back remained stable. |

## Observations — not classified as defects

These observations were recorded for later product/Dev review and were **not used to fail the acceptance run**:

1. **Landscape lock:** changing device orientation does not rotate the game; the game remains landscape.
2. **No zoom:** pinch gesture does not zoom the scene.
3. **Two-finger camera behavior:** two-finger tap and pinch can rotate the camera/object instead of zooming.
4. **One-finger tap:** tapping the house with one finger produced no visible action.
5. **Quest/session message:** during A15 the following text appeared and remained on screen:
   - "Деревенская площадь"
   - "Квест завершен"
   - "+75 ХР, +15 монет"
   - "Новый путь открыт"
6. After a **full application restart**, the quest-completion message was no longer shown; the start state displayed:
   - "Дом героя"
   - "Питомец ждет нового приключения."

## QA conclusion

The installed APK passed the **executed exploratory Android stability/interaction smoke suite** with 20/20 PASS.

This result should **not** be interpreted as 20/20 coverage of the original functional A01–A20 scenarios in this repository. The original functional scenarios involving Village/Forest/Combat/Math/Mastery/Inventory/Equipment still require their own execution when those flows are available in the prototype.

No product code was changed as a result of this manual run.
