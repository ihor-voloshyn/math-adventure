# Math Adventure — Agent Rules

## Authority
- Product Owner: human project owner.
- Approved product requirements and architecture decisions must not be changed silently.
- Agents propose or implement within approved boundaries; unresolved conflicts are reported.

## Document authority
1. 01_PRODUCT_REQUIREMENTS.md
2. Specific system documents 02–21
3. architecture/decisions/ for explicit architectural decisions

## Core boundaries
- Math Engine: mathematical truth, curriculum/skills, skill graph, evaluation, learner mathematical state.
- Mastery System: mastery evaluation/update for learner skills.
- Adaptive Engine: decides what the learner should do next.
- Task Generator: creates concrete task instances.
- Game Engine: turns validated mathematical results into gameplay.

## Adaptive priority
HARD CONSTRAINTS → CRITICAL REMEDIATION → REQUIRED PREREQUISITE → RECENT ERROR RECOVERY → DUE REVIEW → REINFORCE → ADVANCE → INTERLEAVING → CONTEXT VARIETY → DIFFICULTY → FALLBACK

## Task validation
Adaptive Decision → Task Blueprint → Task Generator → Task Instance → Structural Validation → Logical Validation → Math Engine Validation → Validated Task → Game Engine.

## Review rule
Do not silently resolve contradictions. Report the conflict, affected documents, and required Product decision.
