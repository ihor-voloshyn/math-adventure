# Math Adventure — Project Conversation Archive

**Snapshot date:** 2026-09-21  
**Purpose:** Preserve the project context and key decisions available at the baseline snapshot. This file is a project-memory record, not an authoritative product requirements document.

## Roles
- Product Owner: human project owner.
- Project architect / coordinator: ChatGPT.
- Agents may review or implement within approved boundaries; they must not silently change approved requirements or architecture.

## Repository
- GitHub repository: `ihor-voloshyn/math-adventure`
- Main branch: `main`
- Foundation branch: `bootstrap/repository-foundation`
- Baseline backup branch: `backup/initial-documents-2026-09-21`
- Staging directory: `incoming-docs/`
- Authoritative documentation directory: `docs/`

## Working agreement
1. Saved documents are first placed in `incoming-docs/`.
2. The architect compares them with the approved baseline.
3. Contradictions are identified explicitly; Product Owner decisions are not overridden silently.
4. Documents are optimized, normalized, reconciled, and then promoted into `docs/`.
5. `incoming-docs/` remains non-authoritative staging.
6. Architecture changes should be traceable and, where appropriate, recorded as ADRs.

## Approved project direction
Math Adventure is an Android-first, later iOS, 3D fantasy adventure/RPG for primary-school mathematics (grades 1–4 curriculum, but progression based on demonstrated mastery rather than age). Mathematics is embedded in exploration, battles, quests and puzzles rather than presented as a school/test interface.

Core principles:
- adaptive mastery-based learning;
- full primary-school mathematics except geometry initially;
- multilingual;
- offline-first MVP with account and local save;
- future server-authoritative synchronization;
- rewards based on result/quality/mastery, not time spent;
- no pay-to-win or selling mathematical progress;
- MVP single-player;
- 3D from the beginning;
- exploration + tactical turn-based battles + puzzles;
- Math Engine, Mastery System, Adaptive Engine and Task Generator are separate responsibilities.

## Core architecture decisions
**Math Engine**
- Source of mathematical truth.
- Owns curriculum/skills, skill graph/prerequisites, mathematical correctness, attempt evaluation, learner mathematical state and mathematical facts/constraints.
- Does not choose the next pedagogical step, generate exact tasks, issue rewards, or control gameplay.

**Mastery System**
- Owns mastery evaluation/update.
- Mastery range: 0–5.
- Mastery is runtime learner state, not content.

**Adaptive Engine**
- Decides what the child should receive next.
- Reads mastery, history, prerequisites and context.
- Does not calculate answers, validate mathematical truth, generate exact task values/text, update mastery or issue rewards.
- MVP is rule-based, explainable, deterministic when required, and ML-ready architecturally.
- Canonical priority:
  1. Critical Remediation
  2. Required Prerequisite
  3. Recent Error Recovery
  4. Due Review
  5. Reinforce
  6. Advance
  7. Variety
- Hard Constraints are an eligibility filter before pedagogical selection.
- Difficulty is selected after Skill + Mode.

**Task Generator**
- Converts an Adaptive Decision / Task Blueprint into a concrete Task Instance.
- Does not own mathematical truth.
- Validation pipeline:
  Adaptive Decision → Task Blueprint → Task Generator → Task Instance → Structural Validation → Logical Validation → Math Engine Validation → Validated Task → Game Engine.

## Gameplay/world decisions recorded during the project
- Third-person rotatable camera.
- Knight/mage gameplay styles; class does not change the mathematics curriculum.
- Kitten or puppy companion/hero concept; pets can provide small gameplay bonuses.
- Character customization; no gender selection.
- Home grows toward an estate/castle.
- First world includes village, castle/own territory, forest and a small dungeon.
- Main antagonist is a dragon with a later-revealed motivation.
- Visible encounters; player can attack or avoid.
- Tactical turn-based 3D combat; player acts first.
- Wrong math answers affect combat and can pass the turn to the enemy.
- Hearts provide battle/level health; multiple mistakes are possible.
- Defeat protects already saved progress/rewards; current unsaved level rewards can be lost.
- Exploration, quests, battles, puzzles and construction are integrated with mathematics.
- Only Coins in MVP economy; richer resources later.
- No inventory weight in MVP.
- Item rarity and RPG equipment are planned.
- Mathematical hints are free in MVP.
- Day/night is intended to affect enemies, NPCs and places, not merely lighting.
- Player does not directly choose task difficulty; Adaptive Engine controls it.
- New mechanics should be introduced through separate instruction windows.

## Mathematics
Curriculum includes:
- numbers/counting/place value/rounding/estimation;
- addition/subtraction;
- multiplication/division;
- fractions;
- money;
- time;
- measurement;
- word problems;
- logic;
- data/information;
- pre-algebra.
Geometry is excluded initially.

## Known reconciliation items
The project contains earlier document versions with inconsistencies that must be reconciled before final authoritative publication. Known examples include:
- day/night scope;
- whether visual equipment can be hidden while retaining stats;
- allowed nonnumeric input;
- timing of puzzle hints;
- availability of harder tasks for greater rewards;
- item loss rules;
- exact defeat/exit behavior;
- repeat-completed-level behavior;
- introduction of new mechanics;
- exact scope of puzzle hints.

These are to be reconciled against the latest Product Owner answers and approved documents, not guessed.

## Agent strategy
- Initial specialized agent: Architecture/QA reviewer, focused on documents 01–09, boundaries, contradictions, contracts and testability.
- Later agents may cover math/content, game, backend, UI/UX, coding and QA.
- Recommended hierarchy: Architect Agent → specialized agents → QA.
- Human Product Owner remains the final authority on product decisions.

## Snapshot verification
The backup directory `backups/2026-09-21-initial-documents/` contains the complete 10-document snapshot:
- PRODUCT_VISION.md
- 01_PRODUCT_REQUIREMENTS.md
- 02_GAME_DESIGN.md
- 03_WORLD_DESIGN.md
- 04_GAMEPLAY_SYSTEMS.md
- 05_MATH_CURRICULUM.md
- 06_MATH_ENGINE.md
- 07_ADAPTIVE_ENGINE.md
- 08_TASK_GENERATION.md
- 09_ADAPTIVE_ENGINE.md

The backup file blob SHAs were verified against the corresponding files in `incoming-docs/` and match exactly. The backup therefore preserves the source contents byte-for-byte at the Git blob level, while using normalized backup filenames for the staged documents.

## Important note
This archive intentionally preserves project decisions and working context available at the snapshot. It is not a verbatim transcript of every chat message. The authoritative source for product requirements remains the approved project documents and explicit Product Owner decisions.
