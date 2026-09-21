# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Phase 2 — Core Implementation.

## Product decisions recorded
- XP, Coins, and mathematical Mastery are independent MVP systems.
- RPG Level 1–30 is approved for MVP.
- XP Progression v1.0 is approved: XP rewards meaningful game achievements, not individual math answers; cumulative level threshold is 50 × (Level - 1)²; repeated completed content is anti-farmed; XP events are idempotent and offline-first.
- Mastery Progression v1.0 is approved: evidence-based 0–5 progression, diversified evidence for high mastery, delayed verification for Mastery 5, controlled demotion, neutral SKIPPED, hint-aware evidence, offline-first and idempotent attempt processing.
- Game Engine Progression v1.0 is approved: validated game events own XP/Coins/Loot progression; reward calculation and RPG Level calculation are separate from Math/Mastery; progression commits are atomic and idempotent.

## Current work
The project is moving from the core learning vertical slice into explicit game-progression semantics. Mathematical learning remains separated from RPG progression.

## Completed
- Repository foundation and QA workflow.
- Incoming documents preserved and reconciled.
- Canonical documents 01–13 established; document 08 is explicitly reconstructed.
- Domain ownership, math-task pipeline, validation and offline-first contracts established.
- Curriculum / Skill Graph foundation implemented.
- Math Engine mathematical validation foundation implemented.
- Mastery runtime boundary implemented with injected policy.
- Adaptive runtime boundary implemented with injected policy.
- Deterministic Task Generator implemented for the current supported task slice.
- Structural → logical → mathematical validation pipeline implemented.
- End-to-end learning flow implemented: Adaptive → Blueprint → Generator → Validation → Math Engine → Mastery.
- Progression boundary implemented for the mathematical learning flow with explicit RewardPolicy and ProgressionStore extension points.
- RPG Level 1–30 product progression approved.
- XP/RPG Level progression policy approved.
- Mastery progression policy approved.
- Game Engine progression boundary approved.

## Current implementation boundary
Core mathematical progression remains separate from game progression.

The existing CoreProgressionFlow handles validated learning results and its reward extension point; it must not become the source of XP merely because a math answer was submitted.

The new Game Engine progression flow will own validated Game Events, RewardBundle calculation, XP/RPG Level calculation, game unlocks and atomic/idempotent progression commits.

Mastery remains owned exclusively by MasterySystem. Math Engine remains the source of mathematical truth. Persistence stores derived state and does not decide domain semantics.

## Next logical stage
1. Implement Game Engine progression contracts and tests independently from the math-answer reward path.
2. Define Combat semantics using the Game Progression boundary.
3. Continue with Items/Equipment, Pet, Home/Territory, Quests and World/Story.
