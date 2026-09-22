# Math Adventure — Current State

## Last checkpoint
2026-09-22 — Develop MVP 01

## Conversation continuity

The project uses sequential numbered development chats because individual ChatGPT chats can reach their conversation limit.

Sequence:
- Develop MVP 00 — previous development chat.
- Develop MVP 01 — current chat.
- Develop MVP 02, 03, etc. — future continuation chats as limits are reached.

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
- PR #33 and PR #35 are both currently open and both concern connecting COMBAT_VICTORY to Game Progression; they need comparison/consolidation before further progression implementation is treated as canonical.

## Recovery procedure for a new chat

1. Read 00_PROJECT_CONTEXT.md.
2. Read chat/CURRENT_STATE.md.
3. Read decisions/DECISIONS.md.
4. Read the latest files under chat/checkpoints/.
5. Compare those checkpoints with the product/technical documents before continuing.
6. Inspect open PRs and QA status before modifying implementation.
