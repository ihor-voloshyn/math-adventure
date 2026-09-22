# Math Adventure — Current State

## Last checkpoint
2026-09-22 — Develop MVP 01

Quest System foundation merged and QA-validated.

## Conversation continuity

The project uses sequential numbered development chats because individual ChatGPT chats can reach their conversation limit.

Sequence:
- Develop MVP 00 — previous development chat.
- Develop MVP 01 — current chat.
- Develop MVP 02, 03, etc. — future continuation segments of the same Math Adventure project.

A new numbered chat is a continuation of the same Math Adventure project, not a new project.

## Working agreement

When the user writes:

СОХРАН ЧАТ

the assistant treats everything since the previous checkpoint marker as a checkpoint to preserve in the repository, together with the resulting project-state changes.

The checkpoint should:
1. Preserve the relevant conversation text/decisions.
2. Update the current project state.
3. Compare the conversation decisions against repository documents.
4. Record contradictions or unresolved questions instead of silently overwriting decisions.
5. Commit the result to GitHub.

When the user writes «продолжай», recover the repository/project state and continue development.

When the user writes «статус», inspect and report the current project state.

## Important limitation

Saving cannot happen literally after every ChatGPT message without an explicit checkpoint request. The reliable trigger is the marker СОХРАН ЧАТ.

Full-chat archival is part of the project workflow. Between each pair of «сохрани чат» markers, the assistant must preserve the verbatim conversational text available in the active chat in `chat-history/`. Checkpoints/summaries remain a separate fast-recovery layer. The archive is the source for recovering ideas and context; `CURRENT_STATE.md`, checkpoints, and decisions are the fast index. If an older transcript is no longer available in the active context, it must not be fabricated; it is explicitly marked as unavailable/reconstructed.

## Current known state

- Repository: ihor-voloshyn/math-adventure
- Default branch: main
- chat-import/ exists and is reserved for imported conversation history.
- Exact old transcript has not yet been imported.
- Product baseline: 01_PRODUCT_REQUIREMENTS.md v1.0.
- Mathematical architecture boundary: Adaptive Engine → Task Generator → Math Engine.
- Combat vertical slice exists through PR #32.
- PR #33 and PR #35 were duplicate combat-progression attempts and are no longer open; their relevant work was consolidated into PR #36.
- PR #36 was QA-validated and merged into `main` as merge commit `6f8e96d1caeefc1f74ff9940bb3e582e7f15f492`.
- Canonical combat progression now connects `COMBAT_VICTORY → CoreGameProgressionFlow`, with prototype XP/Coins rewards and Android local persistence.
- The current reward tuning is vertical-slice only: 100 XP and 25 Coins for the first victory for a source; final economy values are not yet approved.
- Android persistence is a prototype boundary using one JSON document per player in SharedPreferences with idempotent event handling. It is not the final server-of-truth architecture.
- No unlocks are granted yet by the prototype unlock policy.
- PR #38 added and QA-validated the Quest System foundation, then merged into `main` as `584c7f21ab4efac4ecdd646fedc482fa864497be`.
- Quest System foundation now defines quest states, objectives, prerequisites, completion event identity, and the first Home → Village → Forest → First Battle → Home chain.
- Quest completion now maps to `QUEST_COMPLETED → CoreGameProgressionFlow`; one-time quest rewards are owned by Game Progression and are idempotent.
- PR #39 passed Repository QA, Core Domain QA, and Android Prototype QA and merged into `main` as `1c227ad6f2b680e741e966e6d465220bc9b43851`.
- Android prototype now persists the first quest chain locally and wires Home → Village → Forest → First Battle → Home objectives to quest completion rewards.
- Repeatable quest reward semantics remain explicitly unsupported by the prototype policy until the final repeatable-economy rules are defined.
- Quest System does not own Math Engine, Mastery, Adaptive Engine, or RPG rewards.

## Recovery procedure for a new chat

1. Read 00_PROJECT_CONTEXT.md.
2. Read chat/CURRENT_STATE.md.
3. Read decisions/DECISIONS.md.
4. Read the latest files under chat/checkpoints/.
5. Compare those checkpoints with the product/technical documents before continuing.
6. Inspect open PRs and QA status before modifying implementation.
7. Verify the canonical main branch and obsolete PR/branch references after progression changes.


## Latest implementation checkpoint — Quest progression integration
- Quest completion is now an explicit Game Progression event boundary.
- One-time quest completions grant prototype rewards of 75 XP and 15 Coins; these are vertical-slice tuning, not final economy values.
- Android quest state uses local SharedPreferences prototype persistence.
- The first story chain is executable through the existing visual prototype without moving reward ownership into Quest.
- Next logical implementation block: strengthen the integrated vertical slice with explicit quest/combat integration tests and then proceed to the next MVP gameplay system/content block.


## Latest implementation checkpoint — Integration coverage
- PR #40 added a core integration test for QuestEngine → Game Progression event mapping → reward commit and duplicate idempotency.
- PR #40 passed Repository QA, Core Domain QA, and Android Prototype QA and merged as `622ce9bd63dcef94deb398ba473c301a8783c475`.
- The remaining vertical-slice verification gaps are broader end-to-end acceptance and offline/crash-recovery checks.
- Next logical implementation block: strengthen offline/crash-recovery semantics for combined quest/progression persistence, then continue the next approved gameplay/content block.
