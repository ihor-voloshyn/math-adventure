# ADR-001 — Domain Ownership and Dependency Direction

**Status:** Accepted  
**Date:** 2026-09-21

## Context

The reconciled product/system specifications distribute responsibilities among Curriculum, Math Engine, Mastery System, Adaptive Engine, Task Generator, Game Engine, Economy and Persistence. Several preserved legacy sections used overlapping terminology.

## Decision

Adopt the ownership boundaries in `architecture/contracts/DOMAIN_BOUNDARIES.md`.

The canonical dependency direction is:

`Curriculum / Skill Graph → Math Engine → Mastery System → Adaptive Engine → Task Generator → Validation → Game Engine → Economy / Persistence`

Mastery mutation belongs exclusively to Mastery System. Mathematical correctness belongs to Math Engine. Adaptive chooses the pedagogical next step. Task Generator instantiates concrete tasks.

## Consequences

- Legacy APIs implying Math Engine task selection are not authoritative.
- Game and Economy cannot directly change mathematical progression.
- Cross-domain behavior must use explicit contracts rather than shared ownership.
- Ownership changes require a new ADR and, where product behavior changes, Product Owner approval.

## Alternatives considered

A shared “game/math” service was rejected because it would blur mathematical truth, learner-state ownership and game consequences.
