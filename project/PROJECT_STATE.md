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
3. Child-test the core loop before expanding the asset set.

## Latest implementation checkpoint
- Android prototype camera now supports direct touch rotation on the 3D scene: horizontal drag rotates yaw; vertical drag adjusts pitch within bounded limits.
- This implements the first concrete camera requirement from the approved Visual Style & Asset Specification while keeping the renderer platform-independent from game-domain logic.

## Latest implementation boundary — Quest → Game Progression
Quest completion is now integrated with the Game Progression boundary. Quest remains responsible for objective state and completion identity; Game Progression maps the completion event into XP/Coins rewards and idempotent persistence. The Android prototype persists the first story chain and connects its objectives to the existing Home → Village → Forest → Combat → Home flow.

## Next logical stage
1. Add explicit integration coverage for Quest → Game Progression and the full first story/combat chain.
2. Verify offline/crash-recovery behavior for combined quest and progression persistence.
3. Continue the playable vertical slice with the next approved gameplay/content block without moving mathematical or reward ownership into Quest.

## Future commercial and community direction
A future commercial layer is approved as a planning direction, not as a current product requirement. The MVP remains free/no-ads with monetization scaffold only.

The future plan may use product revenue and/or other project funding to finance:
- production servers and backend infrastructure;
- AI/API and specialized agent usage;
- CI/CD, QA and security infrastructure;
- 3D/content/localization production;
- structured compensation for eligible early users participating in testing and feedback;
- rewards/bounties for external idea contributors.

The future contributor model must remain separate from game progression. Participation, payments and idea rewards must never create gameplay advantages or bypass Math/Mastery/RPG rules.

A future Idea Contributor workflow is planned as:
challenge → submission → product/AI review → prototype → user validation → acceptance → reward.

Before any paid contributor program launches, the project must define appropriate legal, payment, child-safety, consent, attribution and intellectual-property rules. For child testing, participation and any compensation must be handled through parents/guardians as required.

Final product decisions remain with the Founder/Product Sponsor. External ideas can enter the backlog only through the approved product decision process.

## AI Product Studio — strategic decision
Math Adventure is formally the **first product of a broader AI Product Studio**, not the studio itself.

The studio is intended to become a reusable product-development ecosystem for Math Adventure and future products. Its shared layer may include AI/agent orchestration, development workflows, QA, documentation and knowledge management, GitHub automation, testing infrastructure, analytics, human-user testing, contributor/idea workflows, CI/CD, security and commercial infrastructure.

The Studio must be built **evolutionarily from the experience of Product #1** rather than attempting to design a complete studio upfront.

Math Adventure therefore serves two roles:
1. Product #1 — a real standalone product with its own approved scope.
2. Studio learning ground — a real production environment in which agent roles, prompts, workflows, templates, infrastructure and human-validation processes are tested and refined.

Reusable assets created or validated during Math Adventure should be promoted into the Studio only when they are genuinely reusable beyond this product. Product-specific logic must remain inside Math Adventure.

## Studio learning loop
For each important development/process decision, the Studio should distinguish between:
- Math Adventure-specific solution;
- reusable Studio capability;
- experiment/lesson that still needs validation.

The intended evolution is:

Idea → AI research → product definition → prototype → AI development → automated QA → real-user testing → usability/attractiveness/value feedback → AI analysis → iteration → validation → launch.

Real users are a required part of product validation, not merely a final beta stage. Human feedback should assess usability, attractiveness/engagement and perceived value in addition to technical correctness.

## Studio OS — target
The long-term reusable layer is expected to evolve into a Studio Operating System containing:
- agent architecture and roles;
- agent permissions and review gates;
- proven prompts and prompt templates;
- development and QA workflows;
- documentation and decision systems;
- GitHub/project automation;
- testing infrastructure;
- human-user testing and feedback workflows;
- analytics;
- project/product templates;
- security and user-safety rules;
- commercial and contributor workflows.

For future products, the target flow is:
NEW PRODUCT → Studio Template → requirements → architecture → agents → development → QA → real users → validation → commercialization.

Studio maturity must come from measured experience with real products. Prompts, workflows and infrastructure should be versioned/refined based on observed outcomes rather than treated as permanently correct.

## Human-in-the-loop principle
AI may generate, implement, test and analyze, but it must not replace real-user validation of usability, attractiveness or product value.

For products involving children, testing and any compensation must follow applicable child-safety, consent and guardian requirements.

## Long-term studio outcome
The intended outcome is a **project factory**: Product #1 funds and teaches the reusable Studio infrastructure, while later products inherit proven agents, prompts, workflows, templates and human-validation mechanisms.

This direction does not change the approved Math Adventure product requirements or current MVP scope. It is a strategic layer above the product.
