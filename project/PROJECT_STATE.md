# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Phase 2 — Core Implementation.

**Current implementation slice:** Curriculum / Skill Graph + Math Engine foundation.

## Current branch / PR
- Open PR: #6 — `implementation/curriculum-math-engine`
- Target: `main`

## Authoritative structure
- `docs/` — approved/canonical product and system specifications, including `PRODUCT_VISION.md`
- `architecture/` — technical architecture and ADRs
- `project/` — current project state, roadmap, open questions and changelog
- `qa/` — QA strategy and acceptance artifacts
- `incoming-docs/` — staging only; never authoritative
- `backups/` — preserved snapshots; never authoritative

## Completed
- Repository foundation established.
- Repository QA agent and workflow established.
- Incoming documents preserved.
- Initial reconciliation artifacts created.
- Canonical document 07 normalized as Task Generator v1.2.
- Canonical document 08 created as a reconstructed Mastery System v1.0 contract.
- Canonical document 09 frozen as Adaptive Engine baseline.
- Math Engine / Mastery / Adaptive / Task Generator responsibility boundaries documented.
- Phase 1 architecture contracts merged into `main`.
- Phase 2 core Kotlin domain foundation merged into `main`.
- Canonical Product Vision restored to `docs/PRODUCT_VISION.md` and merged via PR #7.

## Current implementation
PR #6 implements the next Phase 2 slice:
- Curriculum / Skill Graph foundation.
- Stable skill IDs and dependency validation.
- Required prerequisite and related-skill queries.
- Math Engine task validation foundation.
- Basic answer evaluation foundation.
- Automated tests for curriculum and Math Engine boundaries.

This is a foundation slice, not the complete 1–4 grade curriculum or complete mathematical solver/generator.

## Canonical responsibility chain
`Curriculum / Skill Graph → Math Engine → Mastery System → Adaptive Engine → Task Generator → Validation → Game Engine → Reward / Persistence`

## Important rules
- Product Owner decisions are authoritative.
- Do not silently change approved requirements.
- When sources conflict, resolve by approved decision/source precedence; ask the Product Owner only when no authoritative resolution exists.
- Adaptive Engine does not optimize difficulty for rewards.
- Mastery System is the sole owner of Mastery updates.
- Math Engine is the source of mathematical truth and validation.
- Task Generator instantiates concrete tasks from Adaptive decisions.
- Chat discussion may explain or propose changes, but a rule becomes project truth only when recorded in the appropriate GitHub artifact.

## Next logical stage
1. Complete and QA PR #6.
2. After merge, implement the Mastery System runtime foundation.
3. Implement Adaptive Engine against the Mastery contract.
4. Implement Task Generator and the validation pipeline.
5. Build the first end-to-end mathematical gameplay vertical slice.
