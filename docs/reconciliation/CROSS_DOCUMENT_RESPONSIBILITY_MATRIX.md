# Math Adventure — Cross-Document Responsibility Matrix

**Status:** Draft  
**Scope:** Documents 01–09 reconciliation  
**Rule:** When two documents describe the same behavior, the component owner below is authoritative for implementation responsibility.

| Concern | Product Requirements | Game / World Design | Gameplay Systems | Math Engine | Mastery System | Adaptive Engine | Task Generator | Game / Economy |
|---|---|---|---|---|---|---|---|---|
| Curriculum / skills | Requirements | Uses skills in content | Consumes | **OWNER** | Reads | Reads | Reads | — |
| Mathematical truth | Requirement | — | — | **OWNER** | Reads evaluation | Must not own | Must not own | — |
| Attempt correctness | Requirement | — | — | **OWNER** | Reads | — | Structural pre-validation only | — |
| Mastery 0–5 | Requirement | — | — | Provides mathematical state/facts | **OWNER** | Reads | — | — |
| Next pedagogical step | Requirement | — | — | — | Provides mastery | **OWNER** | — | — |
| Skill selection | Requirement | — | — | Provides graph/prerequisites | Reads | **OWNER** | Receives blueprint | — |
| Difficulty selection | Requirement | — | — | Provides supported levels | Reads | **OWNER** | Receives selected difficulty | Must not select |
| Exact task values/text | Requirement | — | — | Validates truth | — | Must not generate | **OWNER** | — |
| Structural task validity | Requirement | — | — | — | — | — | Generates then participates in validation | — |
| Logical task consistency | Requirement | — | — | Mathematical validation remains authoritative | — | — | Produces task candidate | Validation layer |
| Mathematical validation | Requirement | — | — | **OWNER** | — | — | Candidate only | — |
| Hint content | Requirement | Game context | UX | Mathematical/content layer | — | Chooses when appropriate | — | — |
| Hint reward effect | Requirement | Describes UX | Describes behavior | MVP: no reward reduction | — | Must not optimize for reward | — | **OWNER of reward calculation** |
| Player difficulty choice | Requirement | — | Explicitly prohibited | — | — | **OWNER of automatic choice** | — | — |
| Combat outcome | Requirement | Defines fantasy | **OWNER of combat flow** | Supplies result | — | Selects pedagogical step | — | Applies reward rules |
| Hearts / defeat | Requirement | Defines experience | **OWNER** | — | — | — | — | Applies persistence/economy |
| Saved Mastery protection | Requirement | — | — | State source | **OWNER of mastery state** | — | — | Persistence layer |
| Best level result | Requirement | Defines repeat UX | **OWNER of repeat flow** | — | — | — | — | **OWNER of reward accounting** |
| Coins | Requirement | Economy fantasy | Uses | Must not own | — | Must not own | — | **OWNER** |
| Equipment stats | Requirement | Item design | **OWNER of equipment gameplay** | — | — | — | — | Economy/inventory |
| Equipment appearance | Requirement | Visual design | **OWNER of presentation rule** | — | — | — | — | — |
| Day/night | Requirement | World definition | **OWNER of runtime effects** | — | — | — | — | — |
| Puzzle hints | Requirement | World/game content | Gameplay UX | — | — | — | — | — |
| Localization | Requirement | Content | Presentation | Mathematical strings where needed | — | Decision metadata | Task text generation | — |

## Canonical runtime chain

```
Curriculum / Skill Graph
        ↓
Math Engine
        ↓
Mastery System
        ↓
Adaptive Engine
        ↓
Task Blueprint
        ↓
Task Generator
        ↓
Structural + Logical Validation
        ↓
Math Engine Validation
        ↓
Validated Task
        ↓
Game Engine
        ↓
Reward / Persistence
```

## Boundary rules

### Math Engine
Owns mathematical truth. It must not choose the next pedagogical step, create exact task instances, or issue game rewards.

### Mastery System
Owns mastery evaluation/update. It must not independently choose the next task or issue game rewards.

### Adaptive Engine
Owns pedagogical selection. It may choose skill, mode and supported difficulty based on learner state and constraints. It must not calculate answers, generate exact task values/text, update mastery, or issue rewards.

### Task Generator
Owns instantiation of a Task Blueprint into a concrete Task Instance. It must not become a second Math Engine.

### Game / Economy
Owns gameplay consequences and reward calculation. Educational progress must not be purchasable or bypassed by economy.

## Reconciliation consequence

Any authoritative document that assigns one of the above responsibilities to another component is a contradiction and must be corrected before implementation.
