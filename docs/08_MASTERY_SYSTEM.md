# 08_MASTERY_SYSTEM.md

**Status:** Reconstruction draft — **not recovered historical approved v1.0**  
**
## 1. Why this reconstruction exists

The preserved snapshot contains no recoverable file whose identity is the intended:

`08_MASTERY_SYSTEM.md v1.0`.

The available numbered documents contain Mastery requirements distributed across Product Requirements, Game Design, Math Curriculum, Math Engine and Adaptive Engine.

Therefore this document reconstructs the **requirements and contract**, not the missing historical wording.

---

## 2. Evidence baseline

The reconstruction is based on:

- `01_PRODUCT_REQUIREMENTS.md` — PR-040–PR-044, PR-049–PR-053, PR-110, acceptance criteria and business rules.
- `02_GAME_DESIGN.md` — adaptive mathematics, cross-context verification, error handling and separation of Game from Mastery.
- `05_MATH_CURRICULUM.md` — Mathematical Mastery as the main mathematical progression gate and separation of curriculum from learner adaptation.
- `06_MATH_ENGINE.md` — Mastery scale, attempt history, error/review behavior, persistence and test expectations.
- `09_ADAPTIVE_ENGINE.md` — Mastery as learner-state input; Adaptive Engine does not update Mastery; exact Mastery rules are delegated to `08_MASTERY_SYSTEM.md`.

The evidence establishes the following contract with high confidence.

---

## 3. Responsibility

### 3.1 Mastery System owns

The Mastery System is responsible for:

1. evaluating evidence about a learner's demonstrated skill;
2. updating Mastery for an individual skill;
3. maintaining the Mastery state on the defined 0–5 scale;
4. distinguishing isolated errors from sustained loss of demonstrated skill;
5. supporting confirmation through varied evidence rather than repeated identical templates;
6. supporting delayed/review evidence;
7. exposing the resulting Mastery state to Adaptive Engine and other authorized consumers.

### 3.2 Mastery System does not own

It must not:

- define the mathematical truth of an answer;
- generate concrete tasks;
- choose the next pedagogical step;
- choose game locations, quests, enemies or actions;
- calculate Coins, RPG XP or inventory rewards;
- make reward optimization decisions;
- replace the Curriculum or Skill Graph.

---

## 4. Mastery scale

The MVP uses a per-skill integer scale:

| Value | Meaning | Evidence interpretation |
|---:|---|---|
| 0 | Not Learned / Unknown | No meaningful evidence or insufficient evidence |
| 1 | Introduction | Initial exposure; isolated simple success is possible |
| 2 | Understanding | Basic principle is forming; errors remain possible |
| 3 | Practice / Working Proficiency | Usually correct in familiar contexts |
| 4 | Confident Application / Stable Proficiency | Stable performance across varied representations/contexts |
| 5 | Mastery | Stable performance, survives delayed checking and variations, supports dependent skills |

The labels are pedagogical states, not percentages.

---

## 5. Core invariants

### M-001 — Per-skill independence

Each mathematical Skill has its own Mastery state.

A high Mastery for Skill A must not automatically increase Skill B.

### M-002 — Mastery is not accuracy percentage

Mastery must not be calculated as a simple:

`correctAttempts / attempts`.

The system must consider multiple evidence dimensions.

### M-003 — One correct answer is insufficient for Mastery 5

A single successful task cannot automatically establish full mastery.

### M-004 — Repeated identical templates are insufficient

Repeated correct answers to the same task pattern must not by themselves establish Mastery 5.

### M-005 — Evidence diversity matters

Confirmation should be possible across:

- direct tasks;
- inverse tasks;
- unknown components;
- different numerical values;
- different wording;
- different representations;
- word problems;
- different gameplay contexts.

### M-006 — Delayed verification matters

A high Mastery state must be checked again after an interval or later learning context.

### M-007 — One error must not erase confirmed mastery

An isolated incorrect attempt must not automatically reset Mastery to zero.

### M-008 — Sustained evidence can lower Mastery

Repeated or sufficiently strong evidence of loss of skill may lower Mastery after additional checking.

The exact downgrade algorithm is **not recoverable from the snapshot and must not be invented here**.

### M-009 — Skipped tasks are neutral evidence

`SKIPPED` must not by itself classify a skill as learned or unlearned.

### M-010 — Hints are evidence, not a reward penalty

Hint usage is recorded. In MVP it does not reduce the game reward and does not block educational progress.

### M-011 — Time is not a mastery requirement

Slow but correct work is not a mathematical failure and must not be converted into a reward/learner-quality penalty merely because it took longer.

---

## 6. Evidence input contract

A Mastery update requires a validated mathematical attempt/result.

Conceptually:

`MasteryUpdateInput`

- `playerId`
- `skillId`
- `attemptId`
- `taskId`
- `timestamp`
- `answerResult`
- `hintUsed`
- `hintLevel` (when applicable)
- `difficulty`
- `contextType`
- evidence metadata required to identify variation/representation

The Math Engine remains the source of mathematical truth. The Mastery System consumes the validated result; it does not independently decide whether the mathematical answer is correct.

---

## 7. Learner Skill State

The preserved documents support a runtime state concept similar to:

`SkillState`

- `skillId`
- `mastery`
- `attempts`
- `correctAttempts`
- `incorrectAttempts`
- `recentErrors`
- `lastAttemptAt`
- `lastCorrectAt`
- `consecutiveCorrect`
- `consecutiveErrors`
- `reviewState`

Not every field must be persisted as a canonical database column. The final data model may derive some values from an append-only attempt history.

---

## 8. Update lifecycle

The intended runtime boundary is:

`Task → validated attempt → Mastery evaluation/update → Adaptive input → Game consequence/reward`

More explicitly:

1. Adaptive Engine selects Skill + Mode + Difficulty.
2. Task Generator creates a concrete task.
3. Structural and logical validation runs.
4. Math Engine validates the mathematical answer/result.
5. Mastery System evaluates the evidence and updates that Skill's Mastery.
6. The resulting Mastery state becomes available to Adaptive Engine.
7. Game/Economy systems independently apply gameplay consequences and rewards.

Mastery update must not be performed by Game Engine or Adaptive Engine.

---

## 9. Diagnostic mode

The system must support diagnostic evidence for:

- initial learner assessment;
- checking skills that appear already mastered;
- establishing an initial per-skill state.

Diagnostic evidence must use the same mathematical truth and Mastery contract; it is not a separate mathematical correctness system.

The exact diagnostic sampling policy is not reconstructed here.

---

## 10. Review and stability

The Mastery System must expose enough state for Adaptive Engine to distinguish:

- newly introduced skills;
- skills still forming;
- skills that need reinforcement;
- skills requiring review;
- skills with recent errors;
- skills with stable high Mastery.

Review scheduling is related learner state, but the exact scheduling algorithm belongs to the finalized Mastery/Adaptive contract and is not recoverable as a historical formula from the snapshot.

MVP does not require a sophisticated forgetting model.

Architecture should allow future use of:

- time since last verification;
- stability of successful evidence;
- history of review outcomes.

---

## 11. Mastery vs progression

Mathematical progression is primarily governed by Mastery and prerequisites.

A game or content gate may ask for a skill state such as:

`AVAILABLE`, `BLOCKED`, or `REQUIRES_REVIEW`.

Game Engine must not calculate Mastery itself.

The exact prerequisite threshold is a policy/configuration concern, not a hard-coded Game Engine rule.

---

## 12. Persistence and atomicity

MVP is offline-first.

Mastery must update without a permanent network connection.

The critical result transaction must preserve consistency between:

- mathematical attempt/result;
- Mastery update;
- saved progress;
- applicable game reward.

The system must not expose a state where a reward is durably granted while its corresponding mathematical attempt is lost, or where Mastery is durably advanced while the corresponding result is lost.

Future backend synchronization may make the server the Source of Truth, but the MVP local state must remain internally consistent.

---

## 13. API boundary

The intended conceptual API should include operations equivalent to:

- `getSkillState(playerId, skillId)`
- `getMastery(playerId, skillId)`
- `applyValidatedAttempt(validatedAttempt)`
- `getProgress(playerId)`

The final technical API may use different names.

The important boundary is:

**Math Engine validates mathematics → Mastery System updates learner mastery → Adaptive Engine consumes mastery.**

---

## 14. Algorithm deliberately not reconstructed

The snapshot does **not** provide enough authoritative evidence to reconstruct:

- a numeric confidence formula;
- exact promotion thresholds from 0→1→2→3→4→5;
- exact downgrade thresholds;
- weighting of difficulty;
- weighting of task diversity;
- exact decay/review intervals;
- exact treatment of hints in the mastery score beyond recording them;
- whether Mastery is updated after every attempt or only after evidence batches;
- exact diagnostic scoring formula.

These must not be silently invented and labeled as approved v1.0 behavior.

A future approved Mastery System document should define these explicitly.

---

## 15. QA requirements derived from the evidence

At minimum, the Mastery implementation must test:

1. independent skill states;
2. one correct answer does not produce automatic Mastery 5;
3. repeated identical templates do not by themselves produce Mastery 5;
4. varied evidence can increase Mastery;
5. one incorrect answer does not reset confirmed Mastery;
6. sustained errors can trigger a downgrade/review path;
7. skipped attempts do not by themselves change learned/unlearned classification;
8. delayed/review evidence is represented;
9. hint use is recorded and does not reduce MVP game reward;
10. Mastery survives level defeat;
11. Mastery survives offline operation and crash recovery;
12. Mastery update is not performed by Game Engine or Adaptive Engine.

---

## 16. Open reconstruction gaps

These are specification gaps, not automatically new PO decisions:

- exact Mastery update algorithm;
- exact review scheduling algorithm;
- exact evidence diversity model;
- exact persistence schema;
- exact diagnostic policy.

They should be resolved when the authoritative `08_MASTERY_SYSTEM.md` is recreated/approved.

---

## 17. Conclusion

The preserved project snapshot gives strong evidence for the **responsibility, state model, scale, invariants and integration contract** of the missing Mastery System.

It does **not** provide sufficient evidence to claim that a historical `08_MASTERY_SYSTEM.md v1.0` has been recovered.

Therefore this file is a reconstruction specification only and must remain clearly distinguished from an approved historical document.
