# Math Adventure — Chat Export / Project Conversation Snapshot

**Export date:** 2026-09-22
**Repository:** `ihor-voloshyn/math-adventure`

> This file is a reconstructed project-history snapshot from the conversation context available to the assistant. It is not a verbatim export of the entire ChatGPT conversation.

## Project

Math Adventure is an Android-first, later iOS, 3D fantasy adventure/RPG for primary-school mathematics (grades 1–4).

Core direction:
- adaptive mathematics based on demonstrated mastery rather than age;
- offline-first;
- local save with future server-authoritative synchronization;
- multilingual;
- no ads;
- RPG progression independent from mathematical Mastery;
- result-based rewards rather than speed;
- exploration, quests, puzzles and turn-based combat;
- stylized child-friendly 3D fantasy;
- knight/mage fantasy;
- kitten or puppy companion;
- home/territory growth toward a castle;
- first major story arc involving a dragon.

## Mathematical architecture

```
Curriculum / Skill Graph
        ↓
Math Engine
        ↓
Mastery
        ↓
Adaptive Engine
        ↓
Task Blueprint
        ↓
Task Generator
        ↓
Structural + Logical Validation
        ↓
Math Validation
        ↓
Game Engine
```

- Math Engine owns mathematical truth, skills, prerequisites, correctness and evaluation.
- Mastery owns per-skill learning state on a 0–5 scale.
- Adaptive Engine chooses the next pedagogical step.
- Task Generator creates concrete task instances.
- Game Engine owns game consequences.
- Game Progression owns XP, Coins, Loot and RPG Level.

## Approved RPG progression

RPG Level: 1–30.

Major milestones:
- 5 — Apprentice Hero
- 10 — Explorer
- 15 — Hero
- 20 — Defender
- 25 — Champion
- 30 — Guardian

Cumulative XP:
```
XPRequired(L) = 50 × (L − 1)²
```

Examples: L1=0, L5=800, L10=4050, L15=9800, L20=18050, L25=28800, L30=42050.

XP is independent from Mastery and Coins.

## Approved Mastery progression

| Level | Meaning |
|---:|---|
| 0 | Unknown / Not Learned |
| 1 | Introduction |
| 2 | Understanding |
| 3 | Working Proficiency |
| 4 | Stable Proficiency |
| 5 | Mastery |

Rules include gradual promotion/demotion, diverse evidence for high mastery, delayed review for Mastery 5, neutral SKIPPED, no speed requirement, and no age-based mastery.

## Adaptive architecture

Adaptive Engine:
- selects Skill, Mode, Difficulty and Context;
- supports Reinforce, Advance, Review and Remediation;
- respects prerequisites;
- is configurable, explainable and deterministic when required for QA;
- does not evaluate mathematical correctness;
- does not modify Mastery;
- does not manage rewards.

## Task generation

Task Generator:
- receives Skill/Difficulty/Mode/Context/Constraints;
- creates concrete tasks, expressions, word problems, options and distractors;
- supports deterministic generation;
- does not choose the pedagogical target;
- does not own Mastery or rewards.

## Approved game systems

Implemented/approved architectural systems include Game Progression, Combat, Items & Equipment, Pet, Home & Territory, Quests, World & Story, and Visual Style/Asset Specification.

First world:
- Home
- Village
- Forest
- small Dungeon
- surrounding paths
- Dragon story thread

## Recent implementation history

### PR #27
First Android visual playable prototype:
```
Home → Village → Forest → Enemy → Math Task → Return Home
```

### PR #28
Connected visual prototype to core learning flow:
```
Adaptive → Task Generator → Validation → Math Engine → Mastery
```

### PR #29
Added the approved Mastery evidence policy implementation.

### PR #30
Added prototype local persistence for learning evidence.

### PR #31
Connected Android prototype to persistent per-skill Mastery and approved adaptive policy.

### PR #32
Introduced the vertical-slice turn-based Combat Engine:
- player first;
- Attack;
- Defend;
- Flee;
- hero hearts;
- enemy HP;
- incorrect math answer causes a miss / enemy turn;
- no automatic death from one error;
- victory emits `COMBAT_VICTORY`;
- combat does not own math correctness or progression rewards.

## Current vertical slice

```
Home
  ↓
Village
  ↓
Forest
  ↓
Enemy Encounter
  ↓
Combat
  ↓
Adaptive Math Task
  ↓
Player Answer
  ↓
Math Engine
  ↓
Combat Resolution
  ↓
Enemy Turn
  ↓
Next Player Turn
```

Combat baseline:
- Hero: 3 hearts
- Enemy: 3 HP
- Correct Attack: 1 damage
- Incorrect Attack: 0 damage + enemy turn
- Defeat preserves confirmed progression
- Flee gives no victory reward

## Next planned architecture

Successful combat should connect to the already approved Game Progression system:

```
COMBAT_VICTORY
      ↓
Game Progression
      ↓
XP / Coins / Loot
      ↓
RPG Level / Unlocks
```

Reward logic must remain outside Combat and Math Engine.

## Conversation/project preference

The project owner prefers autonomous continuation where possible and prefers completed logical stages rather than intermediate progress messages.

## Canonical documents

- `docs/01_PRODUCT_REQUIREMENTS.md`
- `docs/02_GAME_DESIGN.md`
- `docs/03_WORLD_DESIGN.md`
- `docs/04_GAMEPLAY_SYSTEMS.md`
- `docs/05_MATH_CURRICULUM.md`
- `docs/06_MATH_ENGINE.md`
- `docs/07_TASK_GENERATOR.md`
- `docs/08_MASTERY_SYSTEM.md`
- `docs/09_ADAPTIVE_ENGINE.md`
- `docs/10_RPG_PROGRESSION.md`
- `docs/11_XP_PROGRESSION.md`
- `docs/12_MASTERY_PROGRESSION.md`
- `docs/13_GAME_ENGINE_PROGRESSION.md`
- `docs/14_COMBAT_SYSTEM.md`
- `docs/15_ITEMS_EQUIPMENT.md`
- `docs/16_PET_SYSTEM.md`
- `docs/17_HOME_TERRITORY.md`
- `docs/18_QUEST_SYSTEM.md`
- `docs/19_WORLD_STORY.md`

## Verbatim transcript note

The full raw ChatGPT conversation is not available to the GitHub connector as a file or API transcript. This file is therefore a project conversation snapshot/reconstruction, not an exact archival transcript.

If a raw ChatGPT export is later provided, it can be added beside this file without changing this snapshot.
