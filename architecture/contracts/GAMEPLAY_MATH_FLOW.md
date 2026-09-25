# Math Adventure — Gameplay / Math Flow Contract

**Version:** 1.0  
**Status:** APPROVED MVP UI CONTRACT  
**Date:** 2026-09-25

## 1. Purpose

Define the runtime contract connecting the Android MVP math UI with the existing mathematical pipeline and RPG gameplay.

The contract maps the approved Figma flow:

`Math Task → Answer → Math Engine → Gameplay Action → Battle → Reward`

It does not replace the authoritative mathematical or combat contracts. It defines how the UI/gameplay layer consumes them.

## 2. Authoritative boundaries

The existing architecture remains authoritative:

- Adaptive Engine selects `skillId`, `mode`, `difficulty`, `contextType` and constraints.
- Task Generator creates the concrete Task Instance.
- Structural and logical validation validate the generated task.
- Math Engine validates mathematical truth and evaluates the learner attempt.
- Mastery System owns Mastery updates.
- Combat/Game Engine consumes validated gameplay-relevant results.
- Economy/Reward logic applies rewards.

The UI must not duplicate these responsibilities.

## 3. Runtime flow

```
AdaptiveDecision
      ↓
TaskBlueprint
      ↓
TaskGenerator
      ↓
Validated TaskInstance
      ↓
MATH TASK UI
      ↓
Player Answer
      ↓
Math Engine
      ↓
Attempt Result
      ├── INCORRECT → Combat Policy / retry / hint
      │
      └── CORRECT
             ↓
        Mastery evidence
             ↓
        Game Action
             ↓
          BATTLE
             ↓
          REWARD
```

## 4. Math Task UI states

The MVP exposes four explicit presentation states.

### 4.1 IDLE

The task is displayed and no answer has been submitted.

Example:

`7 + 5 = ?`

The UI waits for learner input.

### 4.2 SELECTED

The learner has selected an answer but the attempt has not yet been resolved.

Example:

`12`

Selection is a UI state, not mathematical validation.

### 4.3 CORRECT

Math Engine has validated the submitted answer as mathematically correct.

Example:

`12 ✓`

The UI may transition to the gameplay action.

The UI must not independently calculate correctness.

### 4.4 INCORRECT

Math Engine has validated the submitted answer as incorrect.

Example:

`11 ✕`

MVP behavior:

- show clear feedback;
- allow retry/hint according to the active task and Combat Policy;
- do not treat one error as complete loss of Mastery;
- do not interpret INCORRECT as a successful attack.

The combat consequence of the failed action is owned by Combat Policy, not by the UI or Math Engine.

## 5. Attempt contract

Conceptual UI-to-domain submission:

```text
SubmitAnswer
{
    taskId,
    answer
}
```

The UI sends the learner's answer.

The mathematical layer returns an attempt result conceptually containing:

```text
AttemptResult
{
    taskId,
    skillId,
    result,
    hintUsed,
    evidence
}
```

The exact implementation type may differ, but the ownership boundary must remain unchanged.

## 6. Result handling

### Incorrect

```
Player Answer
    ↓
Math Engine
    ↓
INCORRECT
    ↓
Combat Policy
    ├── miss / action loss
    ├── enemy response
    └── other configured consequence
```

The game does not launch a successful attack.

Incorrect-answer consequences follow the approved Combat System contract and must not be invented by the Math UI.

### Correct

```
Player Answer
    ↓
Math Engine
    ↓
CORRECT
    ↓
Mastery evidence
    ↓
Game Action
    ↓
Attack VFX / animation
    ↓
Enemy state change
    ↓
Quest progress / reward
```

The causal relationship must be visible to the player.

## 7. Battle sequence

The MVP Battle screen exposes four conceptual steps:

1. **SUBMIT** — the accepted mathematical answer enters gameplay.
2. **ATTACK** — the hero performs a class-appropriate action.
3. **DAMAGE** — the target's gameplay state changes when the action succeeds.
4. **REWARD** — XP/progress/reward state is updated when the applicable gameplay condition is met.

For an incorrect attack, Combat Policy may resolve a miss, action loss, enemy response, or another configured consequence. The Math UI must not resolve that consequence itself.

The visual implementation may later use animation and VFX between these states.

## 8. Class-specific action

The same mathematical result can trigger different presentation based on the hero class.

### Knight

```
correct answer
→ physical attack
→ sword / shield / impact VFX
```

### Mage

```
correct answer
→ magical attack
→ staff / crystal / spell VFX
```

Mathematical correctness remains identical; only the gameplay presentation differs.

## 9. Math readability

Math remains the primary readable element of the Math Task screen.

Required:

- large, clear expression;
- clearly separated answer choices;
- unambiguous selected state;
- explicit correct/incorrect feedback;
- optional hint;
- visible task progress.

Avoid:

- time-pressure mechanics;
- decorative effects covering the expression;
- gameplay animation before mathematical validation;
- UI that makes the task look like a school worksheet.

## 10. Error and retry behavior

A wrong answer is a learning event and a combat result, but it is not mathematical defeat by itself.

MVP:

```
INCORRECT
   ↓
feedback
   ↓
Combat Policy
   ├── retry / next task
   ├── miss / action loss
   └── configured enemy response
```

Combat damage/loss remains a Combat/Game Engine concern.

The Math Engine does not change Hearts, HP, Damage, or other gameplay values.

## 11. State ownership

| State / decision | Owner |
|---|---|
| Task selection | Adaptive Engine |
| Concrete task | Task Generator |
| Task structural validity | Validation |
| Mathematical truth | Math Engine |
| Answer correctness | Math Engine |
| Mastery evidence | Math Engine |
| Mastery update | Mastery System |
| Retry/next pedagogical step | Adaptive Engine |
| Attack resolution | Combat System |
| Enemy gameplay state | Combat/Game Engine |
| XP / item reward | Economy / Reward logic |
| UI presentation | Android UI |

## 12. Anti-duplication rule

The Android UI must not contain:

- arithmetic calculation logic;
- Skill selection rules;
- Difficulty selection rules;
- Mastery update rules;
- reward calculation rules;
- independent definitions of mathematical correctness;
- direct manipulation of Hearts/HP as a consequence of mathematical correctness.

The UI renders domain state and sends user actions to the appropriate domain layer.

## 13. QA-critical scenarios

The MVP must later test at minimum:

1. Correct answer → exactly one successful gameplay action.
2. Incorrect answer → no successful attack.
3. Incorrect answer → consequence is resolved only by Combat Policy.
4. Correct answer → enemy gameplay state changes exactly once when the action succeeds.
5. Duplicate submit is rejected/ignored after the attempt is resolved.
6. Hint usage is recorded in the attempt evidence.
7. Mastery is updated by Mastery System, not UI.
8. Task Generator cannot change Mastery.
9. Game/Combat Engine cannot reinterpret mathematical correctness.
10. Same validated answer cannot produce duplicate rewards.
11. Task progress advances only after the intended result.
12. Offline/local replay does not allow duplicate reward application.

## 14. Deterministic event chain

For QA and debugging, the implementation should make the causal event chain observable:

```
TASK_PRESENTED
      ↓
ANSWER_SELECTED
      ↓
ANSWER_SUBMITTED
      ↓
MATH_EVALUATED
      ↓
ATTEMPT_RECORDED
      ↓
[CORRECT]
      ↓
GAME_ACTION_STARTED
      ↓
GAME_ACTION_RESOLVED
      ↓
REWARD_APPLIED
```

For an incorrect attempt:

```
TASK_PRESENTED
      ↓
ANSWER_SELECTED
      ↓
ANSWER_SUBMITTED
      ↓
MATH_EVALUATED
      ↓
ATTEMPT_RECORDED
      ↓
[INCORRECT]
      ↓
COMBAT_RESOLVED
      ↓
FEEDBACK_SHOWN
```

## 15. Relationship to existing contracts

This document extends, but does not replace:

- `architecture/contracts/MATH_TASK_PIPELINE.md`
- `architecture/contracts/VALIDATION_CONTRACT.md`
- `architecture/contracts/DOMAIN_BOUNDARIES.md`
- `docs/06_MATH_ENGINE.md`
- `docs/07_TASK_GENERATOR.md`
- `docs/08_MASTERY_SYSTEM.md`
- `docs/09_ADAPTIVE_ENGINE.md`
- `docs/14_COMBAT_SYSTEM.md`

## 16. Implementation consequence

The repository already contains the core learning flow that connects:

```
Adaptive Engine
    ↓
Task Generator
    ↓
Validation
    ↓
Math Engine
    ↓
Mastery System
```

The next Android implementation slice should expose the UI/game boundary without duplicating that logic:

```
TaskInstance
    ↓
MathTaskViewState
    ↓
SubmitAnswer
    ↓
AttemptResult
    ↓
CombatResolution
    ↓
Gameplay/Reward state
```

The concrete Android framework and package structure must follow the repository's existing implementation baseline rather than introducing a parallel architecture.
