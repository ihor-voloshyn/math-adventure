# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Documentation reconciliation and project foundation.

## Current branch / PR
- Branch: `architecture/phase-1-contracts`
- Target: `main`

## Authoritative structure
- `docs/` — approved/canonical product and system specifications
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

## Current work
1. Review PR #3 before merge.

Repository QA: **passed** on run #27 (`35645899211`).

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
Complete consistency verification of `docs/01–09`, then QA and PR review.
