# Math Adventure — Canonical Document Identity Map

**Status:** Reconciliation working artifact  
**Date:** 2026-09-21

| Canonical document | Intended status | Preserved evidence | Reconciliation status |
|---|---|---|---|
| 01_PRODUCT_REQUIREMENTS.md | Approved v1.0 | incoming + backup 01 | Recoverable |
| 02_GAME_DESIGN.md | Approved v1.0 | incoming + backup 02 | Recoverable |
| 03_WORLD_DESIGN.md | Approved v1.0 | incoming + backup 03 | Recoverable |
| 04_GAMEPLAY_SYSTEMS.md | Approved v1.0 | incoming + backup 04 | Recoverable |
| 05_MATH_CURRICULUM.md | Approved v1.0 | incoming + backup 05 | Recoverable |
| 06_MATH_ENGINE.md | Approved v1.0 | incoming + backup 06 | Recoverable, requires boundary normalization |
| 07_TASK_GENERATOR.md | Approved v1.2 | **incoming 08_TASK_GENERATION.md v1.2, explicitly marked APPROVED** | **Identity mismatch resolved by evidence; content is Task Generator v1.2** |
| 08_MASTERY_SYSTEM.md | Approved v1.0 | No recoverable source found | **Missing historical source; reconstruction exists separately** |
| 09_ADAPTIVE_ENGINE.md | Approved v1.1 | incoming + backup 09, explicitly marked APPROVED | Frozen as normative baseline |

## Identity conclusion

The preserved Task Generation document is explicitly versioned **1.2**, marked **APPROVED**, and defines the exact Task Generator responsibility expected for canonical document 07.

Therefore it is strong evidence that the current staged filename/number is wrong rather than that a separate Task Generator specification is missing.

This does **not** resolve the missing historical Mastery System document 08.

## Promotion rule

Before promotion to `docs/01–09`:

1. Normalize document 07's canonical filename/number while preserving its approved v1.2 content.
2. Keep the missing historical document 08 explicitly separated from the reconstruction draft.
3. Normalize document 06 against the newer responsibility boundaries.
4. Reconcile 01–05 against the canonical decisions and remove/replace contradictory legacy wording.
5. Freeze 09 as the normative Adaptive Engine.
6. Run Repository QA.
