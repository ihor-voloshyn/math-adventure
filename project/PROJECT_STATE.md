# Math Adventure — Project State

**Source of Truth:** GitHub repository  
**Role of chat:** working interface for the project; chat history is not normative.

## Current phase
Phase 2 — Core Implementation.

## Current branch / PR
- Last merged implementation: PR #13, feat: add deterministic task generator and validation foundation.
- Merge commit: f5b259f4fde19f989d1515750b48f34eec25d3c9.
- Working baseline: main.

## Completed
- Repository foundation and QA workflow.
- Incoming documents preserved and reconciled.
- Canonical documents 01–09 established; document 08 is explicitly reconstructed.
- Domain ownership, math-task pipeline, validation and offline-first contracts established.
- Curriculum / Skill Graph foundation implemented.
- Math Engine mathematical validation foundation implemented.
- Mastery runtime boundary implemented with injected policy; no unapproved Mastery algorithm invented.
- Adaptive runtime boundary implemented with injected policy.
- Deterministic Task Generator implemented for the current supported task slice.
- Structural → logical → mathematical validation pipeline implemented.
- Generated-task and mathematical validation tests added.
- PR #13 merged after Repository QA and Core Domain QA passed.

## Current work
Build the first end-to-end domain vertical slice:
1. Adaptive decision
2. Task blueprint
3. Deterministic task generation
4. Structural/logical/mathematical validation
5. Player answer evaluation
6. Mastery update through the injected policy boundary
7. Adaptive consumption of updated state

The exact Mastery promotion/demotion algorithm remains a Product Owner decision and must stay policy-injected.

## Canonical responsibility chain
Curriculum / Skill Graph → Math Engine → Mastery System → Adaptive Engine → Task Generator → Validation → Game Engine → Reward / Persistence

## Important rules
- Product Owner decisions are authoritative.
- Do not silently change approved requirements.
- Adaptive Engine does not optimize difficulty for rewards.
- Mastery System is the sole owner of Mastery updates.
- Math Engine is the source of mathematical truth and validation.
- Task Generator instantiates concrete tasks from Adaptive decisions.
- Chat discussion becomes project truth only when recorded in the appropriate GitHub artifact.

## Next logical stage
Implement and test the first end-to-end core-domain vertical slice without introducing game/economy persistence prematurely.
