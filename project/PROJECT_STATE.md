# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Phase 2 — Core Implementation.

## Product decisions recorded
- XP, Coins, and mathematical Mastery are independent MVP systems.
- RPG Level 1–30 is approved for MVP.
- XP Progression v1.0 is approved: XP rewards meaningful game achievements, not individual math answers; cumulative level threshold is `50 × (Level - 1)²`; repeated completed content is anti-farmed; XP events are idempotent and offline-first.
- Mastery remains the educational progression gate and is never bypassed by RPG Level.

## Current work
The project is moving from the core learning vertical slice into explicit game-progression semantics. Mathematical learning remains separated from RPG progression.

## Completed
- Repository foundation and QA workflow.
- Incoming documents preserved and reconciled.
- Canonical documents 01–09 established; document 08 is explicitly reconstructed.
- Domain ownership, math-task pipeline, validation and offline-first contracts established.
- Curriculum / Skill Graph foundation implemented.
- Math Engine mathematical validation foundation implemented.
- Mastery runtime boundary implemented with injected policy; no unapproved Mastery algorithm invented.
- Adaptive runtime boundary implemented with injected policy.
- Deterministic Task Generator implemented for the current supported task slice.
- Structural → logical → mathematical validation pipeline implemented.
- End-to-end learning flow implemented: Adaptive → Blueprint → Generator → Validation → Math Engine → Mastery.
- Progression boundary implemented: validated learning result → injected RewardPolicy → single ProgressionStore commit boundary.
- RPG Level 1–30 product progression approved.
- XP/RPG Level progression policy approved.

## Current implementation boundary
`CoreProgressionFlow` composes the existing learning flow with two explicit extension points:
- `RewardPolicy` owns reward calculation; no reward formula is hard-coded here.
- `ProgressionStore` owns durable atomic commit mechanics; no storage technology or schema is hard-coded here.

XP is a game-progression concern. It must not be added to the mathematical RewardPolicy merely because a math answer was submitted. A future game-event progression flow should own XP events and RPG Level calculation.

Mastery remains owned exclusively by `MasterySystem`. Math Engine remains the source of mathematical truth. Persistence stores derived state and does not decide domain semantics.

## Next logical stage
1. Define the Mastery progression algorithm and review/forgetting policy.
2. Define the Game Engine progression event boundary for XP/Coins/Loot.
3. Implement XP/RPG Level contracts independently from the math-answer reward path.
4. Continue Game Engine boundary and integration tests without moving mathematical or Mastery ownership into gameplay code.
