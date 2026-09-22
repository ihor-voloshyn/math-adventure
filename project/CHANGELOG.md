# Math Adventure — Changelog

## 2026-09-21
- Established GitHub as the project Source of Truth and chat as the working interface.
- Added project-level recovery artifacts.
- Canonicalized document identity for 07 Task Generator.
- Added reconstructed 08 Mastery System contract.
- Preserved reconciliation and recovery evidence separately from authoritative documentation.
- Started and completed Phase 1 architecture contracts.
- Started Phase 2 with a platform-independent Kotlin core foundation.
- Added Curriculum / Skill Graph and Math Engine foundations.
- Added Mastery and Adaptive runtime boundaries with injected policies.
- Added deterministic Task Generator plus structural/logical/mathematical validation foundation.
- Added generated-task and mathematical validation tests.
- Corrected CI test fixtures and mathematical validation issues; Repository QA and Core Domain QA both passed.
- Merged PR #13 into main as commit f5b259f4fde19f989d1515750b48f34eec25d3c9.

## 2026-09-22
- Recorded the Product Owner decision that XP, Coins, and mathematical Mastery are independent MVP systems.
- Approved RPG Progression v1.0: RPG Level 1–30 with major milestones at Levels 5, 10, 15, 20, 25, and 30. RPG Level develops the hero, world, home, and pet.
- Approved XP & RPG Level Progression v1.0: XP rewards meaningful game achievements rather than individual math answers; repeated completed content is anti-farmed; cumulative XP thresholds use 50 × (Level - 1)²; XP events are idempotent and offline-first; RPG Level never bypasses Mastery.
- Approved Mastery Progression v1.0: evidence-based 0–5 progression with diversified evidence, delayed verification, controlled demotion, neutral SKIPPED, hint-aware evidence and idempotent offline-first processing.
- Approved Game Engine Progression v1.0: validated Game Events drive XP/Coins/Loot through an atomic and idempotent progression commit; math answers do not directly grant XP; RPG Level remains separate from Mastery.

## Next
- Implement Game Engine progression contracts and tests.
- Define Combat semantics using the approved progression boundary.

## 2026-09-22
- Approved Combat System v1.0: turn-based player-first combat, math-result-driven actions, explicit Victory/Defeat boundaries, and separation from Math/Mastery/Adaptive/Reward ownership.

## Next
- Define Items/Equipment semantics.
- Define Pet progression and bonuses.
- Define Home/Territory progression.
- Define Quests and World/Story integration.

## 2026-09-22
- Approved Items & Equipment v1.0: weightless inventory, fixed slots, visible equipment, RPG-level requirements, no direct Mastery gating, atomic rewards and defeat-safe confirmed ownership.

## Next
- Define Pet progression and bonuses.
- Define Home/Territory progression.
- Define Quests and World/Story integration.


## 2026-09-22
- Approved Pet System v1.0: one active kitten/puppy companion, RPG-linked visual growth, small game-owned bonuses, PET_ACCESSORY integration, no mathematical authority or progression bypass, no mandatory care loop, defeat-safe offline-first state and future idempotent sync.

## Next
- Define Home/Territory progression.
- Define Quests and World/Story integration.

## 2026-09-22
- Approved Home & Territory v1.0: persistent player-owned hub, RPG-milestone visual growth, pet/equipment integration, offline-first persistence, idempotent Home Milestones, and controlled MVP scope without city-builder/upkeep complexity.
- Explicitly moved visual development ahead of the first full Playable Prototype: Hero/Pet/NPC/Enemy/Home/Territory form the minimum visual slice.

## Next
- Define Quests and World/Story integration.
- Define the visual asset/style specification and first playable visual slice.
- Begin implementation of the first playable prototype.

## 2026-09-22
- Approved Visual Style & Asset Specification v1.0.
- Set the first child-testable visual slice: Hero, Pet, NPC, Enemy, Home, Territory, basic equipment, UI and VFX.
- Established Stylized 3D Fantasy Adventure as the working visual direction and explicitly moved visual production ahead of the first full Playable Prototype.

## Next
- Define Quests and World/Story integration.
- Build the first visual asset slice.
- Assemble and test the first Playable Prototype.


## 2026-09-22
- Approved Quest System v1.0 with child-readable objectives, first quest chain, offline/idempotent completion and strict ownership boundaries.
- Approved World & Story v1.0 with Home/Village/Forest/Dungeon structure, gradual Dragon story, visible progression and first playable world slice.

## Next
- Implement the first visual asset slice.
- Assemble the first Playable Prototype.
- Child-test the core loop before expanding the Vertical Slice.

## 2026-09-22
- Implemented direct touch rotation for the Android prototype 3D camera, including bounded pitch and continuous yaw.
