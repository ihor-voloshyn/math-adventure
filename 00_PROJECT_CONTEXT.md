# Math Adventure — Project Context

## Purpose
Math Adventure is an Android-first, later iOS, 3D math-learning game for grades 1–4.

## Product principles
- Multilingual.
- No geometry initially.
- Free, no ads; monetization scaffold only.
- Offline-first except synchronization and updates.
- Account system.
- Local save now; future server-of-truth synchronization to prevent reward tampering.
- Rewards are based on results, not time.
- Level unlock costs, failed-attempt refund rules, repeat-level outcome rules.
- Adaptive difficulty.

## World and gameplay
- Rotatable camera.
- Magic + knights are required themes; detective is optional.
- No player-gender choice; customizable outfits/armor.
- Cat and dog pets can accompany mage/knight, grow with progression, and evolve from a small house/bed toward a castle.
- Central plaza, shop, library and quest hub.
- Dragon.
- Dragon defeat is represented through health hearts/progress.
- No minigames.

## Approved product documentation
01_PRODUCT_REQUIREMENTS.md v1.0 is the approved product-requirements baseline.

## Mathematical architecture boundary
Adaptive Engine → Task Generator → Math Engine

- Adaptive Engine chooses Skill / Mode / Difficulty / Context.
- Task Generator creates a concrete task instance using generation constraints.
- Math Engine is the sole source of mathematical truth: it validates generated tasks and evaluates the child's answer.
- Task Generator does not determine the correct answer and does not mutate Mastery.

## Recovery principle
This repository is also the continuity layer for ChatGPT work. Important decisions and conversation checkpoints should be committed here so a new chat can reconstruct the project state without relying on the old conversation's context window.
