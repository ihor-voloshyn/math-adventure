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

## Product decisions recorded
- Combat System v1.0 is approved: turn-based, player-first combat; validated math results resolve actions; Victory emits Game Progression events; Defeat preserves confirmed progress; Combat does not own Math/Mastery/Adaptive/Reward policy.

## Next logical stage
1. Define Items/Equipment semantics.
2. Define Pet progression and bonuses.
3. Define Home/Territory progression.
4. Define Quests and World/Story integration.

## Product decisions recorded
- Items & Equipment v1.0 is approved: weightless MVP inventory, fixed equipment slots, visible equipped appearance, RPG-level gates, no direct Mastery gates, atomic item rewards, no permanent equipment loss on defeat.

## Next logical stage
1. Define Pet progression and bonuses.
2. Define Home/Territory progression.
3. Define Quests and World/Story integration.

- Pet System v1.0 is approved: one active kitten/puppy companion, RPG-linked growth, small game-owned bonuses, no math/Mastery bypass, PET_ACCESSORY integration, offline-first persistence and future idempotent sync.

## Next logical stage
1. Define Home/Territory progression.
2. Define Quests and World/Story integration.

## Current implementation boundary
Home/Territory v1.0 is approved. Home/Territory is a persistent visual/gameplay hub driven by RPG progression, integrated with Pet and Items/Equipment, and independent from Mastery and mathematical authority.

## Next logical stage
1. Define Quests and World/Story integration.
2. Define the visual asset/style specification and first playable visual slice.
3. Begin implementation of the first playable prototype.

## Current implementation boundary
Home/Territory and Visual Style/Asset Specification are approved. The project is now ready to move from system semantics toward the first visual playable slice.

## Next logical stage
1. Define Quests and World/Story integration.
2. Build the first visual asset slice: Hero, Pet, NPC, Enemy, Home, Territory, equipment, UI and VFX.
3. Assemble the first Playable Prototype and test the core loop with a child before expanding the asset set.


## Current implementation boundary
Quest System v1.0 and World & Story v1.0 are approved. The first world loop is now defined from Home through Village/Forest toward Dungeon, with a gradual Dragon story thread.

## Next logical stage
1. Implement the first visual asset slice.
2. Assemble the first Playable Prototype: Home → Village → NPC → exploration → enemy → Math Task → return Home.
3. Child-test the core loop before expanding the Vertical Slice.

## Latest implementation checkpoint
- Android prototype camera now supports direct touch rotation on the 3D scene: horizontal drag rotates yaw; vertical drag adjusts pitch within bounded limits.
- This implements the first concrete camera requirement from the approved Visual Style & Asset Specification while keeping the renderer platform-independent from game-domain logic.


## Latest implementation boundary — Quest → Game Progression
Quest completion is now integrated with the Game Progression boundary. Quest remains responsible for objective state and completion identity; Game Progression maps the completion event into XP/Coins rewards and idempotent persistence. The Android prototype persists the first story chain and connects its objectives to the existing Home → Village → Forest → Combat → Home flow.

## Next logical stage
1. Add explicit integration coverage for Quest → Game Progression and the full first story/combat chain.
2. Verify offline/crash-recovery behavior for combined quest and progression persistence.
3. Continue the playable vertical slice with the next approved gameplay/content block without moving mathematical or reward ownership into Quest.
