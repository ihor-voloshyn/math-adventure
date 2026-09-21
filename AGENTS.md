# Math Adventure — Agent Rules

## Authority and Source of Truth
- **GitHub is the project Source of Truth.**
- Chat is the working interface for discussion, coordination, requests and explanations; chat history is not normative project state.
- Product Owner: human project owner.
- Approved product requirements and architecture decisions must not be changed silently.
- Agents may propose or implement within approved boundaries; unresolved conflicts are reported.
- If a project rule is discussed in chat but is not recorded in the appropriate GitHub artifact, treat the GitHub state as authoritative.

## Information routing
Read only the smallest authoritative set needed for the task:
- Current project status: `project/PROJECT_STATE.md`
- Roadmap: `project/ROADMAP.md`
- Unresolved decisions/gaps: `project/OPEN_QUESTIONS.md`
- Product behavior: `docs/01_PRODUCT_REQUIREMENTS.md`
- Game design: `docs/02_GAME_DESIGN.md`
- World: `docs/03_WORLD_DESIGN.md`
- Gameplay systems: `docs/04_GAMEPLAY_SYSTEMS.md`
- Math curriculum: `docs/05_MATH_CURRICULUM.md`
- Math truth/evaluation: `docs/06_MATH_ENGINE.md`
- Task generation: `docs/07_TASK_GENERATOR.md`
- Mastery: `docs/08_MASTERY_SYSTEM.md`
- Adaptation: `docs/09_ADAPTIVE_ENGINE.md`
- Architecture decisions: `architecture/decisions/`
- QA: `qa/`
- Reconciliation evidence: `docs/reconciliation/`
- Preserved incoming sources: `incoming-docs/`
- Snapshots: `backups/`

Do not read the whole repository by default. Start with `project/PROJECT_STATE.md`, then follow the smallest relevant dependency chain.

## Document authority
1. Approved Product Requirements.
2. Approved/current system specifications.
3. Explicit architecture decisions / ADRs.
4. Reconciliation records and project state for coordination.
5. Incoming documents and backups are evidence/preservation only, not authoritative.

## Core boundaries
- Math Engine: mathematical truth, curriculum/skills, skill graph, evaluation and mathematical validation.
- Mastery System: mastery evaluation/update for learner skills.
- Adaptive Engine: decides what the learner should do next.
- Task Generator: creates concrete task instances from Adaptive decisions.
- Game Engine: turns validated mathematical results into gameplay.
- Reward/Economy: calculates game rewards; Adaptive Engine must not optimize for rewards.

## Adaptive priority
HARD CONSTRAINTS → CRITICAL REMEDIATION → REQUIRED PREREQUISITE → RECENT ERROR RECOVERY → DUE REVIEW → REINFORCE → ADVANCE → INTERLEAVING → CONTEXT VARIETY → DIFFICULTY → FALLBACK

## Task validation
Adaptive Decision → Task Blueprint → Task Generator → Task Instance → Structural Validation → Logical Validation → Math Engine Validation → Validated Task → Game Engine.

## Review rule
Do not silently resolve contradictions. First check approved decisions and source/date/authority precedence. If no authoritative resolution exists, report the conflict and required Product Owner decision.

## Change discipline
- Keep canonical documents focused on their own responsibility.
- Do not duplicate normative rules across documents when a single owner can define them.
- Record non-obvious architecture changes as ADRs.
- Update project state when a logical project stage changes.
