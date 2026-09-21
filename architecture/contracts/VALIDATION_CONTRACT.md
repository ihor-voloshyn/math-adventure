# Math Adventure — Validation Contract

**Version:** 1.0  
**Status:** APPROVED ARCHITECTURE BASELINE  
**Date:** 2026-09-21

## 1. Purpose

Define validation responsibilities and the acceptance gate for generated mathematical tasks.

## 2. Validation layers

1. **Structural Validator** — verifies Task Instance shape and schema.
2. **Logical Validator** — verifies internal task consistency and generator constraints.
3. **Math Engine Validator** — verifies mathematical correctness and expected answer.

## 3. Ordering

Validation is sequential:

`Task Instance → Structural → Logical → Math Engine → Validated Task`

A failed layer stops the task from entering gameplay.

## 4. Determinism and reproducibility

Every generated task must carry enough generation metadata to reproduce or diagnose generation failures. The exact RNG implementation is an implementation detail.

## 5. Runtime answer validation

For a learner answer:

- nonnumeric tasks use selection input;
- numeric tasks use numeric free input;
- arbitrary free-text mathematical input is outside MVP.

Math Engine is authoritative for mathematical correctness.

## 6. Error handling

Validation failure is a system/content defect, not a learner error.

Learner incorrectness is recorded only after a valid task has been accepted and the learner submits an answer.

## 7. Quality gate

No unvalidated Task Instance may be exposed to the learner.

Validation must be independently testable at unit and integration levels.
