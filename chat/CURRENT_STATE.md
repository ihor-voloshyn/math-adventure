# Math Adventure — Current State

## Last checkpoint
2026-09-23 — checkpoint 0003 (Develop MVP 01)

Checkpoint 0003 preserves the current implementation and continuation state after the Items & Equipment → Inventory → Equipment → Combat integration sequence. The exact full transcript for this interval was not fully available in active context, so unavailable conversation text was not fabricated.

## Working agreement
When the user writes «продолжай», inspect the repository, open PRs and QA first, then continue the next logical implementation stage without asking for confirmation.
When the user writes «сохрани чат», preserve the available conversation context as a checkpoint/archive, update current state and durable decisions when needed, and commit the result to GitHub.

## Repository
- Repository: ihor-voloshyn/math-adventure
- Default branch: main

## Current implementation state

### Items & Equipment
- PR #43 added the approved Items & Equipment core foundation and merged as `41720bc9acc40f329ae10abc66ab1d2e681a5e3f`.
- PR #44 connected Game Progression loot to Inventory through deterministic loot creation and an atomic progression+loot boundary; merged after QA.
- PR #45 added Android Inventory/Equipment persistence and combat-victory loot wiring; merged after QA.
- PR #46 added Android Equip/Unequip controls, persistence and visual weapon state; merged after QA.
- PR #47 connected Equipment stats to Combat; attack damage and starting Hearts are consumed by Combat while Inventory/Equipment remains the source of state; merged after QA.
- PR #48 hardened Equipment → Combat state invariants and merged after QA.

### Open PR
- PR #49: `fix: harden Android inventory persistence`
- Head: `47e2003b2c3089bc21ceb263faec8d5d0c612d1a`
- Status at checkpoint: open, awaiting CI/QA.
- Scope: validate inventory player ownership, known item IDs, duplicate instance IDs, equipped ownership/non-stacking, and SharedPreferences commit failure. Keep progression + loot in one local persistence boundary.

## Architecture boundaries
- Math Engine is authoritative for mathematical truth.
- Adaptive Engine owns learning selection, not correctness, Mastery mutation or rewards.
- Task Generator creates concrete tasks, not authoritative answers.
- Game Progression owns XP/Coins/Loot.
- Inventory/Equipment owns item ownership and equipped state.
- Combat consumes resolved combat stats and does not mutate Inventory/Equipment.
- Visual equipment state derives from Equipment + Inventory + ItemDefinition.
- Android SharedPreferences is prototype persistence only; final server-of-truth sync remains future work.

## Known gaps
- Android end-to-end persistence acceptance for Loot → Inventory → Equip → Restart/Load → Combat/Visual is not yet claimed complete.
- Combat defense is resolved but not yet applied to enemy damage.
- Final XP/Coins economy and unlock policy remain unapproved vertical-slice tuning.
- Child/device acceptance is not claimed.

## Next continuation
1. Check PR #49 CI/QA.
2. If all required QA passes, merge PR #49 and verify main.
3. Continue Android persistence/integration acceptance coverage.
4. Then proceed to the next approved gameplay block while preserving the established ownership boundaries.

## Archive
- Checkpoint: `chat/checkpoints/0003-2026-09-23.md`
- Chat archive checkpoint: `chat-history/2026-09-23_develop-mvp-01_checkpoint-03.md`
