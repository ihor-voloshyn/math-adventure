# Math Adventure — Reconciliation Report for Documents 01–09

**Status:** Draft reconciliation report  
**Source:** `incoming-docs/` snapshot on `main`  
**Date:** 2026-09-21  
**Authority:** Approved Product Owner decisions and approved project direction recorded in project memory

## 1. Executive result

The staged documents contain a coherent core architecture, but they are **not yet safe to promote as the authoritative `docs/01–09` set**.

The most important findings are:

1. **Document numbering/content mismatch:** staged 07 is an Adaptive Engine document, while staged 08 is Task Generation and staged 09 is another Adaptive Engine document. The intended authoritative model is 07 Task Generator, 08 Mastery System, 09 Adaptive Engine.
2. **Day/night scope conflict:** World Design treats day/night as a gameplay system affecting enemies/NPCs/places; Gameplay Systems currently says MVP day/night is primarily visual, while also giving night enemy effects.
3. **Equipment appearance conflict:** Gameplay Systems allows hiding the visual appearance of equipped equipment while retaining stats; the approved Product Owner decision is that equipped visual equipment **cannot be hidden while retaining its stats**.
4. **Hint reward conflict:** Game Design says using a hint may reduce additional quality reward; Gameplay Systems and Math Engine say hints do not reduce reward in MVP. Approved direction is **free hints in MVP without reducing the game reward**.
5. **Hint UX/scope needs normalization:** Gameplay Systems describes contextual hints and no permanently available hint button, while other documents use broader generic hint wording. Approved direction is that puzzle hints are introduced later (decision C); exact MVP scope must be stated consistently.
6. **Adaptive Engine duplication:** 07 and 09 both define Adaptive Engine responsibilities. Document 09 is the newer normative Adaptive Engine baseline; 07 must not be promoted as authoritative Adaptive Engine if the intended 07 is Task Generator.
7. **Difficulty/reward coupling needs explicit wording:** the documents correctly state that the child does not directly choose mathematical difficulty, but some text describes higher difficulty as associated with higher reward. This must remain a game/economy consequence, not an Adaptive Engine objective or a player-selectable reward optimization mechanism.
8. **Defeat/retry rules are mostly aligned but need one canonical formulation:** saved progress/best result is protected, current failed attempt may lose unsaved level rewards, defeat returns to level entry, and the attempt cost is refunded. These rules should be stated once canonically and referenced elsewhere.
9. **Repeat-completed-level rules need canonicalization:** repeat is for improving the previous result; a weaker/equal result must not destroy the best result. Economic net-zero behavior must be consistent across Product Requirements and Game Design.
10. **Input wording needs normalization:** numeric free input is required; non-numeric mathematical answers in MVP use selection from offered options rather than arbitrary text input.

## 2. Findings by severity

### BLOCKER — document identity / numbering

**Evidence**
- `incoming-docs/07_ADAPTIVE_ENGINE.md — Adaptive Engine.md` is an Adaptive Engine specification.
- `incoming-docs/08_TASK_GENERATION.md — Генерация математических заданий.md` is Task Generator.
- `incoming-docs/09_ADAPTIVE_ENGINE.md` is also Adaptive Engine and is explicitly marked v1.1 APPROVED.

**Expected canonical set**
- 07 — Task Generator
- 08 — Mastery System
- 09 — Adaptive Engine

**Impact**
The repository cannot safely expose these files as `docs/07`, `docs/08`, `docs/09` without a mapping decision. In particular, no staged document currently represents the intended authoritative Mastery System document.

**Action**
Do not promote the current numbered files directly. Preserve the backup and create a canonical document map first.

**PO input required:** YES, only if the missing 08 Mastery System source cannot be recovered from prior project artifacts. The existing project baseline says 08_MASTERY_SYSTEM.md v1.0 was approved, so recovery should be attempted before drafting a replacement.

---

### MAJOR — day/night scope

**Evidence**
- World Design §22 defines a day/night cycle and states it affects the world beyond decoration, including night-specific enemies.
- Gameplay Systems §3.11 says day/night is present in MVP primarily as a visual world state, while also stating that night affects enemies.

**Conflict**
"Primarily visual" is narrower than the approved gameplay direction that day/night affects enemies, NPCs and places.

**Resolution**
Canonical wording should define day/night as an MVP gameplay system with at least observable effects on:
- enemies;
- NPCs;
- places/available interactions;
- visual state.

It must not be reduced to lighting only.

**PO input required:** NO — use the latest approved decision.

---

### MAJOR — equipment visual appearance

**Evidence**
Gameplay Systems §15.6 states that the player may preserve a desired appearance independently of equipped item statistics.

**Approved direction**
Equipped visual equipment cannot be hidden while retaining the item's gameplay statistics.

**Resolution**
Remove/replace the transmog/hidden-appearance rule from authoritative gameplay documentation.

**PO input required:** NO.

---

### MAJOR — hint reward behavior

**Evidence**
- Game Design §40 says hint use may reduce an additional quality reward.
- Math Engine §19 says hint use must not reduce the game reward in MVP.
- Gameplay Systems §11.5 says hint use does not reduce the reward.
- Product Requirements says hints do not block educational progress and separately defines reward by result/quality.

**Resolution**
For MVP:
- hints are free;
- hint use is recorded;
- hint use does not reduce the game reward;
- hints do not destroy educational progress;
- hint content is owned by the appropriate mathematical/content layer, not Adaptive Engine.

If future versions introduce quality-reward modifiers, that belongs in an explicit future economy/reward specification, not in the MVP rules.

**PO input required:** NO.

---

### MAJOR — puzzle hint timing/scope

**Evidence**
Gameplay Systems contains contextual hint behavior and a separate hint section. The project decision says puzzle hints are introduced later (decision C).

**Resolution**
Authoritative docs must distinguish:
- mathematical hints for solving math tasks;
- puzzle hints for world/game puzzles.

Puzzle hints should not be described as an always-available MVP facility. Their later introduction must be explicit.

**PO input required:** NO for the already approved timing; YES only if a new exact puzzle-hint UX is needed beyond that decision.

---

### MAJOR — Adaptive Engine duplication

**Evidence**
Both staged 07 and staged 09 contain extensive Adaptive Engine specifications. Staged 09 is marked `09_ADAPTIVE_ENGINE.md v1.1 — APPROVED` and defines the canonical responsibilities, priorities, contracts and runtime pipeline.

**Resolution**
Treat document 09 as the normative Adaptive Engine baseline. Do not merge two Adaptive Engine documents by simple concatenation. Recover the intended Task Generator document for 07 and Mastery System document for 08.

**PO input required:** NO for choosing 09 as the current Adaptive baseline.

---

### MAJOR — difficulty vs reward boundary

**Evidence**
Adaptive Engine documents state that Adaptive Engine chooses difficulty and that higher difficulty may be associated with higher game reward. They also correctly state that the player does not directly choose difficulty.

**Resolution**
Canonical architecture:
- Adaptive Engine chooses pedagogical difficulty.
- Game/Economy Systems determine rewards from validated result and game rules.
- Higher difficulty may be a factor in reward calculation only where explicitly defined by Economy/Reward rules.
- Adaptive Engine must never optimize difficulty for coins or other rewards.
- Player must not be offered a direct difficulty selector as a way to farm rewards.

**PO input required:** NO.

---

### MAJOR — defeat / retry / unsaved reward semantics

**Evidence**
Product Requirements and Game Design describe safe retry behavior and protection of best results. Game Design additionally states that after defeat the player returns to the beginning of the level. Product Requirements says the current failed attempt is lost while confirmed Mastery/best result is preserved.

**Canonical rule**
1. Losing all hearts ends the current attempt.
2. Confirmed educational progress/Mastery is preserved.
3. The best completed-level result is preserved.
4. Unsaved rewards belonging only to the failed current attempt may be lost.
5. Attempt cost is refunded according to the MVP retry rule.
6. The player may immediately retry.
7. After defeat, the player returns to the level entry/start state.

**PO input required:** NO.

---

### MAJOR — repeat completed level

**Evidence**
Product Requirements §17 and Game Design §§45–47 define best-result preservation, deposit/new-result behavior and net-zero rules for weaker/equal repeats.

**Resolution**
Use one canonical rule:
- completed levels can be repeated only to improve the previous result;
- weaker result: best result remains unchanged;
- equal result: best result remains unchanged;
- better result: best result is replaced by the new result;
- repeat attempts must not create an unintended infinite economic farming loop.

The exact reward/deposit calculation should be owned by the Reward/Economy specification, not duplicated with slightly different formulas across gameplay documents.

**PO input required:** NO.

---

### MINOR — input terminology

**Evidence**
Product Requirements requires numeric free input and selection-based answers. Adaptive Engine v1.1 explicitly states that non-numeric mathematical answers in MVP use offered choices rather than arbitrary text input.

**Resolution**
Use:
- numeric free input where the task requires a numeric result;
- selection for non-numeric answers;
- no arbitrary free-text mathematical answer input in MVP.

**PO input required:** NO.

## 3. Architecture boundary check

The staged documents are broadly consistent with the intended separation:

```
Curriculum
    ↓
Math Engine
    ↓
Mastery System
    ↓
Adaptive Engine
    ↓
Task Generator
    ↓
Validation
    ↓
Game Engine
    ↓
Reward / Progress
```

The strongest normative boundary currently present is in document 09:

- Math Engine owns mathematical truth and evaluation.
- Mastery System owns mastery update.
- Adaptive Engine chooses the next pedagogical step.
- Task Generator creates concrete task instances.
- Game/Economy systems handle game rewards.

These boundaries should be preserved during reconciliation.

## 4. Recommended reconciliation order

1. Recover the authoritative 08 Mastery System source.
2. Recover the authoritative 07 Task Generator source (expected v1.2).
3. Freeze 09 as the current Adaptive Engine baseline.
4. Reconcile Product Requirements, Game Design, World Design and Gameplay Systems against the latest PO decisions.
5. Reconcile Math Engine, Mastery System, Adaptive Engine and Task Generator contracts.
6. Produce a cross-document responsibility matrix.
7. Produce an explicit list of unresolved PO decisions only where the existing baseline genuinely does not determine the answer.
8. Promote only the reconciled documents into `docs/`.
9. Run Repository QA against the promoted set.


---

## 6. Detailed Mastery reconstruction findings

A dedicated reconstruction specification has now been added at docs/reconciliation/MASTERY_SYSTEM_RECONSTRUCTION.md.

The distributed evidence is sufficiently consistent to reconstruct the following with high confidence:

- Mastery is per Skill and uses a 0–5 scale.
- Mastery is not a raw accuracy percentage.
- One correct answer or repeated identical templates cannot establish Mastery 5.
- Evidence diversity and delayed verification are required for stable mastery.
- One isolated error cannot reset confirmed Mastery.
- Sustained evidence can lower Mastery, but the exact downgrade algorithm is not recoverable.
- SKIPPED is neutral evidence.
- Hints are recorded, do not block educational progress, and do not reduce MVP game rewards.
- Mastery is learner runtime state, not content.
- Mastery System updates Mastery; Adaptive Engine consumes it.
- MVP Mastery must work offline and remain consistent with saved attempt/progress state.

### Important unresolved implementation details

The snapshot does not contain enough authoritative information to reconstruct:

- promotion/demotion formulas;
- confidence weighting;
- exact diversity scoring;
- exact review intervals;
- exact diagnostic scoring;
- whether updates are per-attempt or batched.

These must remain explicit specification gaps rather than invented rules.

---

## 7. Additional boundary conflicts discovered

### MAJOR — Math Engine API wording vs Adaptive ownership

06_MATH_ENGINE.md contains an older conceptual API in which generateTask(context) exists and a later section says that Math Engine itself can choose a suitable mathematical skill.

This conflicts with the newer canonical architecture in which:

- Adaptive Engine chooses Skill + Mode + Difficulty;
- Task Generator creates the concrete task;
- Math Engine provides mathematical truth and validates the result.

Resolution: the older generateTask(context) / “Math Engine chooses skill” wording must be treated as legacy conceptual material and normalized during promotion. It must not be implemented as Math Engine ownership of adaptive selection.

PO input required: NO — the newer responsibility matrix and document 09 establish the intended boundary.

### MAJOR — MathResult exposes masteryAfter while Mastery owns updates

06_MATH_ENGINE.md defines a conceptual MathResult containing masteryBefore and masteryAfter, while document 09 explicitly states that Adaptive Engine does not change Mastery and the architecture assigns Mastery update responsibility to the Mastery System.

Resolution: the final contract should not make Math Engine the owner of Mastery mutation. A mathematical result may carry a snapshot/reference to learner state for integration purposes, but the authoritative update operation belongs to Mastery System.

PO input required: NO.

### MINOR — confidence terminology

Math Engine uses a confidence field and language such as “increase confidence” without defining whether confidence is a persisted learner-state field, an internal intermediate metric, or merely descriptive wording.

Resolution: do not promote confidence as a canonical public field until the Mastery System specification defines it. The reconstruction draft deliberately leaves the formula and storage semantics open.


## 8. Current status

**Reconciliation is in progress.**

No authoritative document has been overwritten or promoted yet.

The incoming snapshot remains the source-preservation layer. The Mastery reconstruction is explicitly labeled as a reconstruction draft, not recovered historical v1.0.
