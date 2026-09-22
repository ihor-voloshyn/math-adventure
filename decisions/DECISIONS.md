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

The command «сохрани чат» must preserve the full verbatim conversational text available between checkpoint markers in `chat-history/`. In parallel it must update `chat/CURRENT_STATE.md`, create/update a checkpoint, update durable decisions when needed, compare against canonical project documents, and commit the result. The full-chat archive is the source for recovering ideas, context, and the reasoning history. Checkpoints/summaries are a fast index so a new «продолжай» does not require reading the whole archive first. Older transcript text that is no longer available must never be invented; it must be marked as unavailable or reconstructed.

## D-005 — Sequential development chat numbering
Status: Approved

Development chats are numbered sequentially because ChatGPT conversations can reach their maximum length. Develop MVP 00, Develop MVP 01, Develop MVP 02, etc. are continuation segments of the same Math Adventure project. A new numbered chat must recover state from GitHub checkpoints and canonical documents rather than starting a new project.

## D-007 — Consolidated combat victory progression
Status: Approved

PR #36 consolidated the previously duplicate PR #33/#35 work. The canonical implementation is a single COMBAT_VICTORY → CoreGameProgressionFlow integration with prototype XP/Coins reward, idempotent Android local persistence, and persisted unlock state. PR #36 passed Repository QA, Core Domain QA, and Android Prototype QA, then was merged into `main` as `6f8e96d1caeefc1f74ff9940bb3e582e7f15f492`.

The current 100 XP / 25 Coins reward values are vertical-slice tuning, not final economy decisions.

## Open questions

- Final XP/Coins economy values remain intentionally unspecified by the vertical slice.
- Define the final unlock policy when the RPG progression and content-unlock implementation is ready.
- Replace prototype local progression persistence with the planned server-of-truth synchronization architecture before production.
