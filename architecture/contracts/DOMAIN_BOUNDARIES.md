# Math Adventure — Domain Boundaries Contract

**Version:** 1.0  
**Status:** APPROVED ARCHITECTURE BASELINE  
**Date:** 2026-09-21

## 1. Purpose

Define authoritative ownership boundaries between mathematical, pedagogical, task-generation, gameplay, economy, and persistence domains.

## 2. Authoritative ownership

| Domain | Owns | Must not own |
|---|---|---|
| Curriculum / Skill Graph | mathematical skill catalog, prerequisites, curriculum structure | learner mastery, task instances, rewards |
| Math Engine | mathematical truth, task/result validation, validated attempt evidence | pedagogical selection, Mastery mutation, Coins |
| Mastery System | per-skill Mastery state and updates | mathematical truth, task generation, difficulty selection, rewards |
| Adaptive Engine | next pedagogical decision: Skill/Mode/Difficulty/Context and constraints | Mastery mutation, reward optimization, concrete task generation |
| Task Generator | concrete Task Instance from Task Blueprint | skill selection, Mastery, mathematical authority |
| Validation | structural, logical, mathematical validation pipeline | pedagogical decisions, rewards |
| Game Engine | game flow, world, combat, quests, application of validated math result | mathematical truth, Mastery mutation |
| Economy | Coins and game rewards/consequences | mathematical progression truth, Mastery |
| Persistence | durable local/runtime state and atomic recovery | domain policy and mathematical truth |
| Sync (future) | reconciliation of server-authoritative state | local domain rules, task correctness |

## 3. Canonical dependency direction

`Curriculum / Skill Graph → Math Engine → Mastery System → Adaptive Engine → Task Generator → Validation → Game Engine → Economy / Persistence`

Dependencies must not create reverse ownership.

## 4. Key invariants

1. A game event cannot directly mutate Mastery.
2. Adaptive cannot select difficulty to maximize rewards.
3. Task Generator cannot invent mathematical constraints outside the Blueprint/Curriculum.
4. Math Engine remains authoritative for mathematical correctness.
5. Economy cannot grant mathematical progression.
6. Persistence stores state; it does not decide state semantics.
7. Every concrete task must pass structural, logical, and mathematical validation before gameplay use.

## 5. Change policy

Changes that alter ownership or dependency direction require an ADR and Product Owner approval when they change approved product behavior.
