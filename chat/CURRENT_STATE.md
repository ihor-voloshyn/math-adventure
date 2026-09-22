# Math Adventure — Current State

## Last checkpoint
2026-09-22

## Conversation continuity
The previous Math Adventure ChatGPT conversation reached the maximum conversation length. Its complete transcript is not automatically available in a new chat.

A full ChatGPT export can later be imported and the Math Adventure portion extracted into chat-import/.

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

## Important limitation
Saving cannot happen literally after every ChatGPT message without an explicit checkpoint request. The reliable trigger is the marker СОХРАН ЧАТ.

## Current known state
- Repository: ihor-voloshyn/math-adventure
- Default branch: main
- chat-import/ exists and is reserved for imported conversation history.
- Exact old transcript has not yet been imported.
- Product baseline: 01_PRODUCT_REQUIREMENTS.md v1.0.
- Mathematical architecture boundary: Adaptive Engine → Task Generator → Math Engine.

## Recovery procedure for a new chat
1. Read 00_PROJECT_CONTEXT.md.
2. Read chat/CURRENT_STATE.md.
3. Read decisions/DECISIONS.md.
4. Read the latest files under chat/checkpoints/.
5. Compare those checkpoints with the product/technical documents before continuing.
