# Math Adventure — Current State

## Last checkpoint
2026-09-23 — checkpoint 0004 (Develop MVP 01)

Checkpoint 0004 preserves the latest implementation state and the newly confirmed Hero/Pet concept.

## Working agreement
When the user writes «продолжай», inspect the repository, open PRs and QA first, then continue the next logical implementation stage without asking for confirmation.
When the user writes «сохрани чат», preserve the available conversation context as a checkpoint/archive, update current state and durable decisions when needed, and commit the result to GitHub.

## Repository
- Repository: ihor-voloshyn/math-adventure
- Default branch: main

## Current implementation state
- PR #43 Items & Equipment foundation merged.
- PR #44 Game Progression loot → Inventory merged.
- PR #45 Android Inventory/Equipment persistence + combat loot wiring merged.
- PR #46 Android Equip/Unequip + persistence + visual weapon state merged.
- PR #47 Equipment stats → Combat merged.
- PR #48 Equipment → Combat invariant validation merged.
- PR #49 Android inventory persistence hardening merged.
- PR #50 Equipment state validation before persistence merged.
- PR #51 Android MVP acceptance matrix merged.
- PR #52 is currently open: fix: show actual combat heart maximum.
  - Base main SHA: 1b71339f52734edcabe12d8d367330ddd8a6e9ae
  - Head SHA: 9f30bcc945b06f1e21d37435ecddee43d405f80d
  - Scope: CombatState carries maxHeroHearts; HUD displays actual maximum; core test covers a 5-heart combat state.
  - It has not been claimed merged; CI/QA must be checked before merge.

## Confirmed Hero concept
- The Hero is the character itself: a kitten or puppy.
- The player chooses Cat or Dog at the start.
- There is no gender-selection step.
- Cat/Dog is not a separate pet.
- The Hero grows visually through progression: kitten/puppy → young → adult hero.
- Hero development supports Knight and Mage directions.
- Equipment is visually worn by the Hero.
- Appearance can be customized through fur/appearance, clothing, armor, weapons, artifacts and accessories.
- Hero gameplay stats are separate from mathematical Mastery.

## Confirmed separate Pet system
- Separate companion pets can exist in addition to the Cat/Dog Hero.
- Initial concept list: rabbit, fox, dragon, owl, squirrel, turtle, hedgehog, frog.
- MVP should expose only a small subset; other pets can unlock progressively.
- Pets can grow/develop visually.
- Pets may provide gameplay bonuses such as Attack, Defense, Hearts, Ability Power or other Combat Modifiers.
- Pets do not directly modify Mastery, Adaptive Engine decisions, or mathematical correctness.
- Pet bonuses are conceptually equivalent to equipment/other gameplay bonuses.
- The pet catalog remains extensible; the full final roster is not yet locked.

## Architecture boundaries
- Math Engine is authoritative for mathematical truth.
- Adaptive Engine owns learning selection, not correctness, Mastery mutation or rewards.
- Task Generator creates concrete tasks, not authoritative answers.
- Game Progression owns XP/Coins/Loot.
- Inventory/Equipment owns item ownership and equipped state.
- Combat consumes resolved combat stats and does not mutate Inventory/Equipment.
- Visual equipment state derives from Equipment + Inventory + ItemDefinition.
- Android SharedPreferences is prototype persistence only; final server-of-truth sync remains future work.
- Mastery remains a learning-system state and is not modified by RPG Level, equipment or pet bonuses.

## Current gameplay stat model
- Base combat hearts without equipment: 3.
- Equipment can add Hearts; current prototype sword gives Attack +1 and no Hearts bonus.
- Combat HUD fix PR #52 makes the displayed maximum match the actual combat start value.
- Attack and Hearts are currently consumed by Combat; Defense is resolved but not yet applied to enemy damage.
- XP/RPG Level, Equipment, Abilities, Pets and Combat Modifiers belong to game progression; Mastery remains separate.

## Known gaps
- PR #52 QA/merge status still needs verification.
- Android runtime/device acceptance A01–A20 is not claimed complete.
- Combat defense application remains intentionally unresolved.
- Final XP/Coins economy and unlock policy remain unapproved vertical-slice tuning.
- Final server synchronization is future work.

## Next continuation
1. Inspect PR #52 CI/QA.
2. If all required QA passes, merge PR #52 and verify main.
3. Continue Android MVP runtime/integration acceptance; do not claim device acceptance from CI alone.
4. Preserve the Hero/Pet concept and existing ownership boundaries.

## Archive
- Checkpoint: chat/checkpoints/0004-2026-09-23.md
- Chat archive: chat-history/2026-09-23_develop-mvp-01_checkpoint-04.md
