# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Phase 2 — Core Implementation.

## Product decisions recorded
- XP, Coins, and mathematical Mastery are independent MVP systems. XP drives RPG/character progression; Coins are the game currency; Mastery remains the educational progression gate.
- RPG Level 1–30 is approved for MVP. Every fifth level is a major milestone; RPG Level develops the hero, world, home, and pet without replacing or bypassing Mastery. XP thresholds and exact level rewards remain separate decisions.

## Current work
The first core-domain vertical slice is complete. The next slice establishes the boundary from validated learning results into game rewards and durable progression without inventing an unapproved reward formula or storage schema.

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

## Current implementation boundary
`CoreProgressionFlow` composes the existing learning flow with two explicit extension points:
- `RewardPolicy` owns reward calculation; no reward formula is hard-coded here.
- `ProgressionStore` owns durable atomic commit mechanics; no storage technology or schema is hard-coded here.

Mastery remains owned exclusively by `MasterySystem`. Math Engine remains the source of mathematical truth. Persistence stores derived state and does not decide domain semantics.

## Next logical stage
Implement concrete offline persistence only after the durable schema/technology boundary is explicitly selected. In parallel, continue the Game Engine boundary and integration tests without moving mathematical or Mastery ownership into gameplay code.
