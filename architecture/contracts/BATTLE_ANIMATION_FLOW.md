# Battle Animation Flow Contract

Version: 1.0  
Status: APPROVED MVP RUNTIME CONTRACT  
Date: 2026-09-25

## Purpose

Define the runtime boundary between mathematical resolution, combat resolution, and visible attack animation.

## Correct answer

The authoritative sequence is:

`ANSWER_SUBMITTED → MATH_EVALUATED(CORRECT) → ATTACK_STARTED → ATTACK_RESOLVED → REWARD`

UI behavior:

1. Math Task enters `CORRECT`.
2. Battle UI enters `AttackStarted`.
3. Android renderer plays the attack presentation.
4. The coordinator resolves the pending combat outcome.
5. Battle UI enters `AttackResolved`.
6. Hit feedback is shown.
7. If the enemy is defeated, the reward flow starts.
8. Otherwise the next math task is presented.

The combat state must not be visually committed before `ATTACK_RESOLVED`.

## Incorrect answer

The sequence is:

`ANSWER_SUBMITTED → MATH_EVALUATED(INCORRECT) → ATTACK_RESOLVED(MISS) → RETRY`

Requirements:

- no attack animation;
- no successful damage;
- the current task remains active;
- answer options become available again;
- Combat Policy remains responsible for any future incorrect-answer consequence.

## Responsibilities

- Math Engine / Core Learning Flow: mathematical correctness.
- MathBattleFlowCoordinator: bridges mathematical result to UI/combat state and holds pending attack outcome.
- Combat Engine: authoritative combat outcome and resulting combat state.
- BattleStateMachine: visible battle state transitions.
- Android renderer: animation and visual feedback only.
- Reward/Progression: reward application after the intended combat result.

## Duplicate protection

While `AttackStarted` is active:

- answer input remains locked;
- a second attack submission is not accepted;
- pending combat resolution can be consumed once;
- reward application remains idempotent.

## Visual timing

The current Android prototype uses a short presentation window before resolving a successful attack. The exact duration is a presentation parameter and must not affect mathematical or combat correctness.
