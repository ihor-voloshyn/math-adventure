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

## Open questions
None added in this checkpoint.
