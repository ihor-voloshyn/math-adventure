# Math Adventure — Decisions

This file records durable decisions that should survive chat changes.

## D-001 — Product baseline
Status: Approved

01_PRODUCT_REQUIREMENTS.md v1.0 is the approved product requirements baseline.

## D-002 — World direction
Status: Approved

The core world combines magic and knights. The player has no gender-selection step. Outfits/armor are customizable. Pets are cats/dogs with progression from a small home toward a castle. The world includes a central plaza, shop, library, quest hub and dragon.

## D-003 — Mathematical responsibility boundary
Status: Approved

Architecture:
Adaptive Engine → Task Generator → Math Engine

The Math Engine owns mathematical truth, including validation of generated tasks and answer evaluation. The Task Generator only instantiates tasks from constraints.

## D-004 — Chat continuity
Status: Approved

The GitHub repository is the durable project memory. The marker СОХРАН ЧАТ requests a checkpoint. Checkpoints preserve conversation decisions and allow later comparison against repository documents.

## D-006 — Two-layer chat memory: full archive + checkpoint
Status: Approved

The command «сохрани чат» must preserve the full verbatim conversational text available between checkpoint markers in chat-history/. In parallel it must update chat/CURRENT_STATE.md, create/update a checkpoint, update durable decisions when needed, compare against canonical project documents, and commit the result. The full-chat archive is the source for recovering ideas, context, and the reasoning history. Checkpoints/summaries are a fast index so a new «продолжай» does not require reading the whole archive first. Older transcript text that is no longer available must never be invented; it must be marked as unavailable or reconstructed.

## D-005 — Sequential development chat numbering
Status: Approved

Development chats are numbered sequentially because ChatGPT conversations can reach their maximum length. Develop MVP 00, Develop MVP 01, Develop MVP 02, etc. are continuation segments of the same Math Adventure project. A new numbered chat must recover state from GitHub checkpoints and canonical documents rather than starting a new project.

## D-007 — Consolidated combat victory progression
Status: Approved

PR #36 consolidated the previously duplicate PR #33/#35 work. The canonical implementation is a single COMBAT_VICTORY → CoreGameProgressionFlow integration with prototype XP/Coins reward, idempotent Android local persistence, and persisted unlock state. PR #36 passed Repository QA, Core Domain QA, and Android Prototype QA, then was merged into main.

## D-008 — Quest System foundation
Status: Approved

PR #38 added the first executable Quest System foundation from approved docs/18_QUEST_SYSTEM.md: quest definitions/states, objective progression, prerequisite boundary, completion event identity, and the first onboarding chain. Quest System remains separate from Math correctness/Mastery/Adaptive logic and does not grant RPG rewards directly.

## D-009 — Quest completion to Game Progression integration
Status: Approved

PR #39 connected Quest System completion to Game Progression. Quest completion maps to QUEST_COMPLETED events; Game Reward Policy owns XP/Coins; one-time quests are anti-farmed by stable quest sourceId; event IDs remain idempotent.

## D-010 — Quest/Game Progression integration coverage
Status: Approved

PR #40 added core integration coverage for QuestEngine completion → QUEST_COMPLETED → Game Progression reward commit, including duplicate event idempotency and persisted XP/Coins state.

## D-011 — First playable vertical slice acceptance
Status: Approved

PR #42 added executable core acceptance coverage for Home → Village → Forest → Combat → Return Home using the real QuestEngine, CombatEngine, QuestProgressionCoordinator and Game Progression.

## D-012 — Items & Equipment core foundation
Status: Approved

PR #43 implemented the approved Items & Equipment domain foundation: item definitions, item instances, weightless inventory, consumable stacking, equipment slots, RPG-level gates, equip/unequip and replacement semantics.

## D-013 — Items/Equipment ownership boundary hardening
Status: Approved

Inventory + Equipment are the source of owned/equipped item state. Combat receives resolved immutable combat stats and does not mutate item state. An equipped instance must be owned, match its declared equipment slot, be a separate non-stacked instance, and cannot occupy multiple equipment slots. Android local persistence validates these invariants before saving state.

## D-014 — Checkpoint 0003 continuation
Status: Approved

After checkpoint 0003, development resumes by verifying PR #49 CI/QA, merging it only after required checks pass, then completing Android persistence/integration acceptance. Combat defense remains intentionally unresolved until a deliberate combat-stat design step.

## D-015 — Hero identity: kitten or puppy
Status: Approved

The Hero is the character itself, not a human avatar. At the start the player chooses a kitten or puppy. There is no gender-selection step. The Hero grows visually through progression and can develop toward Knight or Mage. Equipment is worn directly by the Hero and can customize appearance. A Cat/Dog is therefore not a separate pet.

## D-016 — Separate companion pets
Status: Approved

Separate companion pets may exist alongside the Cat/Dog Hero. Initial concept list: rabbit, fox, dragon, owl, squirrel, turtle, hedgehog, frog. MVP should expose only a small subset, with further pets unlocked progressively. Pets may provide gameplay bonuses such as Attack, Defense, Hearts, Ability Power or other Combat Modifiers, in the same conceptual layer as equipment and other gameplay bonuses. Pets must not directly modify Mastery, Adaptive Engine decisions, or mathematical correctness. The final pet roster remains extensible and is not fully locked.
