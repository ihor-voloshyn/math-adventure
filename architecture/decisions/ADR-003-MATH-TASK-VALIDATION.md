# ADR-003 — Generated Task Validation Gate

**Status:** Accepted  
**Date:** 2026-09-21

## Context

Generated mathematical content can fail independently at schema, logical-consistency or mathematical-correctness levels. Learner errors must not be confused with content defects.

## Decision

Every generated Task Instance passes, in order:

`Structural Validation → Logical Validation → Math Engine Validation`

Only a fully validated task may be presented to the learner.

Validation failure is a system/content defect. Learner incorrectness is recorded only against a valid task.

## Consequences

- Each validation layer is independently testable.
- Task generation and mathematical truth remain separate.
- Invalid generated content cannot contaminate Mastery evidence.
- Generation metadata must support reproducibility/diagnosis.

## Alternatives considered

Allowing partially validated tasks into gameplay was rejected because it could create false learner errors and invalid Mastery evidence.
