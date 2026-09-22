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

## D-005 — Sequential development chat numbering
Status: Approved

Development chats are numbered sequentially because ChatGPT conversations can reach their maximum length. Develop MVP 00, Develop MVP 01, Develop MVP 02, etc. are continuation segments of the same Math Adventure project. A new numbered chat must recover state from GitHub checkpoints and canonical documents rather than starting a new project.

## Open questions

- PR #33 and PR #35 both appear to implement the COMBAT_VICTORY → Game Progression connection. They must be compared before further implementation.
