# Android MVP Acceptance Plan

Status: IN PROGRESS  
Scope: first playable Android vertical slice after PR #50.

## Goal

Validate the complete player-facing loop without expanding product scope:

Home → Village → Forest → Enemy Encounter → Combat → Adaptive Math Task → Math Engine → Mastery → Combat Result → Game Progression → XP/Coins/Loot → Inventory → Equipment → Persistence → Restart/Reload.

## Acceptance matrix

| ID | Scenario | Expected result | Evidence |
|---|---|---|---|
| A01 | First launch | App starts at Home and initializes local state | Android run |
| A02 | Home → Village | First quest starts/progresses and village is reachable | Android run + persisted quest state |
| A03 | Village → Forest | Story objective progresses and forest is reachable | Android run |
| A04 | Forest → Combat | Combat starts with current equipment-derived stats | Android run |
| A05 | Math task | Task is generated through Adaptive → Generator → Validation → Math Engine | Core tests + Android run |
| A06 | Correct answer | Attack resolves and enemy loses the configured attack damage | Core tests + Android run |
| A07 | Incorrect answer | Attack deals no damage and enemy turn occurs; one error does not instantly defeat hero | Core tests + Android run |
| A08 | Mastery evidence | Answer is processed by Mastery independently of RPG rewards | Core tests + Android run |
| A09 | Victory progression | Victory emits one Game Progression event and awards XP/Coins/Loot | Core tests + Android run |
| A10 | Loot persistence | sword_sparks appears in Inventory after victory | Android run |
| A11 | Equip | Sword can be equipped only from owned Inventory state | Core tests + Android run |
| A12 | Equipment persistence | Equipped sword survives Activity/process restart | Android run |
| A13 | Visual consistency | Equipped visual state matches persisted Equipment state | Android run |
| A14 | Combat stat effect | Equipped sword increases attack damage according to its definition | Core tests + Android run |
| A15 | Duplicate victory | Repeating the same completed encounter does not grant the same progression reward again | Core tests + Android run |
| A16 | Defeat | Confirmed progression is preserved after combat defeat | Core tests + Android run |
| A17 | Flee | Flee ends combat without victory reward | Core tests + Android run |
| A18 | Invalid equipment state | Persistence rejects unowned, duplicated, stacked, or wrong-slot equipment | Core tests |
| A19 | Recovery | Interrupted/offline-compatible local progression can recover without duplicate rewards | Existing recovery tests + Android run |
| A20 | Clean restart | Existing local state is restored without corrupting quest/progression/inventory/equipment | Android run |

## Current automation boundary

The repository has strong Core Domain and Android build/test CI, but no dedicated Android instrumentation (androidTest) suite is currently present. Therefore CI success alone must not be treated as device-level MVP acceptance.

The remaining acceptance work is split into:

1. Core automated verification for domain invariants and idempotency.
2. Android runtime verification on an emulator/device for persistence, restart/reload, visual state and the full player-facing loop.

## Explicit non-goals

- No Windows desktop version.
- No browser MVP.
- No iOS implementation in this acceptance stage.
- No backend synchronization.
- No production economy balancing.
- No final defense-stat combat design.
- No additional world/content expansion before the vertical slice is validated.

## Exit criteria

Phase 3 Android MVP validation can be marked complete only when A01–A20 have evidence, with Android runtime scenarios explicitly tested rather than inferred from compilation.
