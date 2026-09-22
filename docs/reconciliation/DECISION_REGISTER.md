# Math Adventure — Reconciliation Decision Register

## Decisions already determined — no PO action required

| ID | Topic | Canonical rule |
|---|---|---|
| R-001 | Day/night | MVP day/night is a gameplay system, not lighting only; it affects enemies, NPCs and places/interactions as well as visuals. |
| R-002 | Equipment appearance | Equipped visual equipment cannot be hidden while retaining its gameplay statistics. |
| R-003 | Math hints | MVP mathematical hints are free and do not reduce game rewards. |
| R-004 | Puzzle hints | Puzzle hints are introduced later; they are distinct from mathematical task hints. |
| R-005 | Difficulty choice | The player does not directly select mathematical difficulty; Adaptive Engine controls it. |
| R-006 | Difficulty/reward boundary | Adaptive Engine selects pedagogical difficulty; Game/Economy calculates rewards. Adaptive Engine must not optimize for rewards. |
| R-007 | Defeat | Loss of all hearts ends the current attempt; confirmed Mastery/best result remains protected; current unsaved attempt rewards may be lost; retry is available; defeat returns to level entry. |
| R-008 | Repeating completed levels | Repeat is for improving the saved result; weaker/equal attempts cannot destroy the best result; reward accounting must prevent unintended farming. |
| R-009 | Input | Numeric answers use numeric input; non-numeric answers use selection; arbitrary free-text math input is not MVP scope. |
| R-010 | New mechanics | New mechanics are introduced through dedicated instruction windows. |
| R-015 | XP / Coins / Mastery | MVP keeps XP, Coins, and mathematical Mastery as three independent systems. XP represents RPG/character progression; Coins are the game currency; Mastery represents mathematical learning progress and controls access to mathematically dependent progression. RPG Level must not replace or override Mastery as the educational progression gate. |
| R-016 | RPG Level progression | MVP uses RPG Level 1–30. Every level provides a game-progression reward or unlock; every fifth level is a major milestone. RPG Level develops the hero, world, home, and pet. RPG Level must not replace or bypass Mastery. |

## Recovery items — investigation required before asking PO

| ID | Topic | Current evidence | Action |
|---|---|---|---|
| R-011 | Document 07 | Staged 07 is Adaptive Engine, but expected canonical 07 is Task Generator v1.2. | Resolved: staged Task Generation v1.2 is explicitly APPROVED and is canonical 07 content; normalize identity during promotion. |
| R-012 | Document 08 | Historical approved wording was not recoverable from preserved artifacts. | Resolved by creating a reconstructed canonical 08 contract, explicitly labeled as reconstruction rather than historical recovery. |
| R-013 | Document 09 | Staged 09 is Adaptive Engine v1.1 approved. | Freeze as current Adaptive baseline. |
| R-014 | Source of Truth | Project knowledge must not depend on chat-history reconstruction. | Resolved: GitHub is the Source of Truth; chat is the working interface. Project state is partitioned under project/, with domain truth in docs/, architecture in architecture/, and QA in qa/. |

## R-017 — XP and RPG Level progression

**Status:** Approved by Product Owner delegation ("делай на свое усмотрение"). The success criteria are child engagement and real learning progress.

MVP rules:
- XP rewards meaningful game achievements, not individual math answers.
- Main sources: quests, combat victories, first-time exploration/discovery, puzzles, story milestones, and home/territory milestones.
- Repeating completed content does not provide full XP again; improvement can provide a limited bonus.
- Wrong math answers and math hints do not directly reduce XP.
- Cumulative XP threshold is 50 × (Level - 1)² for Levels 1–30.
- XP events are idempotent by unique eventId.
- XP works offline and is later server-validatable.
- RPG Level never bypasses Mastery or mathematical prerequisites.

## R-018 — Mastery progression algorithm

**Status:** Approved by Product Owner delegation ("делай на свое усмотрение").

MVP Mastery is evidence-based and uses a 0–5 state machine. Promotion is gradual; one correct answer cannot create Mastery 5; one error cannot reset Mastery. Mastery 4/5 requires evidence diversity and appropriate difficulty. Mastery 5 requires delayed verification. SKIPPED is neutral. Hints are not errors. Time is not a mastery requirement. Demotion is controlled and requires sustained negative evidence. Mastery policy is configurable, offline-first and idempotent by attemptId. RPG Level, XP and Game Engine do not modify Mastery.

## R-019 — Game Engine progression boundary

**Status:** Approved by Product Owner delegation ("делай на свое усмотрение").

MVP rules:
- Game events are the source events for XP, Coins and Loot; individual math answers do not directly grant XP.
- A validated game event passes eligibility/repeat policy, reward policy, RPG level calculation and unlock calculation before one atomic progression commit.
- eventId provides idempotency; retries must not duplicate rewards.
- First completion may receive the full eligible reward; repeats without best-result improvement do not grant XP; improvement may grant limited bonus XP.
- Defeat never removes confirmed XP, RPG Level, Mastery or confirmed rewards; current uncommitted run rewards may be lost.
- RPG Level 1–30 remains separate from Mastery and cannot bypass mathematical prerequisites.
- Progression is offline-first and designed for later server validation.

## R-020 — Combat system

**Status:** Approved by Product Owner delegation ("делай на свое усмотрение").

MVP rules:
- Combat is turn-based and the player acts first.
- Math Engine is the sole authority for mathematical correctness; Combat consumes validated attempt results.
- Incorrect answers can cause missed/failed actions but do not automatically cause defeat or reset Mastery.
- SKIPPED is distinct from INCORRECT.
- Victory creates a COMBAT_VICTORY Game Event; rewards are calculated by Game Progression, not Combat.
- Defeat preserves confirmed XP, RPG Level, Mastery and confirmed rewards; current uncommitted run rewards may be lost.
- RPG Level and equipment may affect game combat parameters but cannot bypass required mathematical prerequisites.
- Combat difficulty and Math Difficulty are separate concepts.
- No real-time response speed requirement is used as a mathematical competence gate.

## R-021 — Items & Equipment

**Status:** Approved by Product Owner delegation ("делай на свое усмотрение").

MVP rules:
- Inventory has no weight limit.
- Equipment uses a small fixed slot model and has visible equipped appearance.
- Rarity is an RPG property, not a measure of mathematical mastery.
- RPG Level may gate equipment; Mastery does not directly gate equipment.
- Defeat does not destroy confirmed equipment or inventory.
- Item acquisition flows through Game Event / Reward / Inventory boundaries and is idempotent.
- Complex crafting, upgrades and mandatory set bonuses are out of MVP.
