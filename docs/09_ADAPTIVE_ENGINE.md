# 09_ADAPTIVE_ENGINE.md

**Проект:** Math Adventure  
**Документ:** Adaptive Engine — адаптивная система обучения  
**Версия:** 1.1  
**Статус:** APPROVED  
**Дата:** 2026-09-21

---

# 1. Назначение

`Adaptive Engine` определяет, **какой математический шаг должен получить ребёнок следующим**.

Adaptive Engine анализирует текущее состояние обучения ребёнка и выбирает:

- Skill;
- Mode;
- Difficulty;
- Context Type;
- необходимые ограничения для Task Generator.

Главный принцип:

> **Math Engine отвечает на вопрос «что ребёнок знает и что математически произошло?». Adaptive Engine отвечает на вопрос «что ребёнку дать дальше?».**

---

# 2. Архитектурная роль

Adaptive Engine находится между состоянием математических знаний ребёнка и генерацией следующего задания.

```text
┌──────────────────────┐
│     MATH ENGINE      │
│                      │
│ Skill Graph          │
│ Mastery              │
│ Attempt Results      │
│ Learning History     │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   ADAPTIVE ENGINE    │
│                      │
│ Skill Selection      │
│ Mode Selection       │
│ Difficulty Selection │
│ Review               │
│ Reinforcement        │
│ Remediation          │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   TASK GENERATOR     │
│                      │
│ Concrete Task        │
└──────────────────────┘
```

---

# 3. Что делает Adaptive Engine

Adaptive Engine:

- анализирует состояние Skills;
- учитывает Mastery;
- учитывает prerequisites;
- анализирует историю ошибок;
- определяет необходимость remediation;
- определяет необходимость review;
- определяет необходимость reinforcement;
- определяет возможность advance;
- выбирает следующий Skill;
- выбирает Mode;
- выбирает Difficulty;
- выбирает математический контекст;
- обеспечивает interleaving;
- обеспечивает context variety;
- формирует `AdaptiveDecision`.

---

# 4. Что Adaptive Engine НЕ делает

Adaptive Engine не должен:

- решать математические задачи;
- вычислять правильные ответы;
- валидировать математическую истину;
- изменять Mastery напрямую;
- генерировать конкретные числа;
- создавать конкретный текст задания;
- создавать distractors;
- выдавать Coins;
- выдавать XP;
- управлять RPG;
- управлять боем;
- управлять UI;
- определять возраст ребёнка как Skill;
- самостоятельно выбирать случайный Skill без учёта состояния обучения.

---

# 5. Граница с Math Engine

Math Engine отвечает:

```text
Что произошло математически?
Какой ответ правильный?
Как изменилось состояние Skill?
Какой текущий Mastery?
Какие prerequisites существуют?
```

Adaptive Engine отвечает:

```text
Что ребёнку делать дальше?
Какой Skill выбрать?
Нужно ли повторение?
Нужно ли remediation?
Можно ли двигаться дальше?
Какую Difficulty выбрать?
```

---

# 6. Граница с Task Generator

Adaptive Engine не создаёт конкретное задание.

Он формирует:

```text
Task Blueprint
```

Task Generator превращает Blueprint в:

```text
Task Instance
```

---

# 7. Общий pipeline

```text
Player Answer
      ↓
Math Engine
      ↓
Attempt Evaluation
      ↓
Mastery Update
      ↓
Adaptive Engine
      ↓
Adaptive Decision
      ↓
Task Blueprint
      ↓
Task Generator
      ↓
Task Instance
      ↓
Validation
      ↓
Game Engine
```

---

# 8. Основные входные данные

Adaptive Engine получает:

```text
AdaptiveInput
├── learnerState
├── skillStates
├── mastery
├── skillGraph
├── attemptHistory
├── recentErrors
├── reviewState
├── currentContext
├── currentMode
├── availableTaskTypes
├── availableRepresentations
└── systemConstraints
```

---

# 9. Learner State

Adaptive Engine использует состояние конкретного ребёнка.

Пример:

```text
LearnerState
├── learnerId
├── skillStates
├── recentAttempts
├── reviewSchedule
├── currentProgression
└── adaptiveHistory
```

Mastery является частью состояния знаний ребёнка и не хранится в Content Model.

---

# 10. Skill State

Для каждого Skill Math Engine предоставляет состояние.

Например:

```text
SkillState
├── skillId
├── mastery
├── attempts
├── correctAttempts
├── incorrectAttempts
├── recentErrors
├── lastAttemptAt
└── reviewState
```

---

# 11. Mastery

Mastery имеет диапазон:

```text
0–5
```

Пример интерпретации:

```text
0 — не освоено / неизвестно
1 — начальное знакомство
2 — формирующееся понимание
3 — рабочее освоение
4 — устойчивое освоение
5 — высокая устойчивость
```

Точные правила изменения Mastery определяются `08_MASTERY_SYSTEM.md`.

Adaptive Engine **не изменяет Mastery**.

---

# 12. Mastery как вход Adaptive Engine

Adaptive Engine использует Mastery как один из основных входов.

Например:

```text
Mastery = 0
→ remediation / prerequisite

Mastery = 1–2
→ reinforce

Mastery = 3
→ reinforce / review

Mastery = 4
→ review / advance

Mastery = 5
→ advance / review
```

Это не означает прямого жёсткого соответствия.

Приоритет зависит также от:

- ошибок;
- prerequisites;
- review schedule;
- истории;
- текущего режима;
- контекста;
- других педагогических условий.

---

# 13. Главный принцип приоритетов

Adaptive Engine не должен выбирать Skill просто по наименьшему Mastery.

Сначала определяется **педагогическая необходимость**.

Канонический порядок:

```text
1. Critical Remediation
2. Required Prerequisite
3. Recent Error Recovery
4. Due Review
5. Reinforce
6. Advance
7. Variety
```

---

# 14. Hard Constraints

До педагогического выбора применяются:

```text
HARD CONSTRAINTS
```

Они определяют, какие варианты вообще допустимы.

Например:

- Skill отключён;
- контент недоступен;
- отсутствует необходимый Template;
- отсутствует Generator capability;
- отсутствует необходимая локализация;
- Skill недоступен в текущем режиме;
- технические ограничения;
- контентная версия несовместима.

Hard Constraints являются **фильтром допустимости**, а не педагогическим приоритетом.

---

# 15. Канонический алгоритм

```text
1. HARD CONSTRAINTS
2. CRITICAL REMEDIATION
3. REQUIRED PREREQUISITE
4. RECENT ERROR RECOVERY
5. DUE REVIEW
6. REINFORCE
7. ADVANCE
8. INTERLEAVING
9. CONTEXT VARIETY
10. DIFFICULTY SELECTION
11. FALLBACK
```

---

# 16. Critical Remediation

Critical Remediation используется, когда ребёнок показывает серьёзную проблему, препятствующую дальнейшему обучению.

Примеры:

- устойчивые повторяющиеся ошибки;
- несколько неудачных попыток;
- невозможность выполнить базовое действие;
- нарушение prerequisite;
- систематическая ошибка одного типа.

В этом случае remediation имеет приоритет над продвижением.

---

# 17. Required Prerequisite

Если выбранный Skill требует prerequisite, который недостаточно освоен, Adaptive Engine должен вернуть ребёнка к необходимому prerequisite.

Пример:

```text
MUL_TABLE
    requires
MUL_EQUAL_GROUPS
```

Если `MUL_EQUAL_GROUPS` недостаточно освоен:

```text
не давать напрямую сложный MUL_TABLE
```

а выбрать:

```text
MUL_EQUAL_GROUPS
```

---

# 18. Recent Error Recovery

Недавняя ошибка сама по себе не означает обязательную remediation.

Однако повторяющаяся или характерная ошибка может повысить приоритет Skill.

Например:

```text
Attempt 1 → error
Attempt 2 → correct
Attempt 3 → error
```

Adaptive Engine может назначить дополнительную проверку.

---

# 19. Один ошибочный ответ

Одна ошибка:

```text
НЕ РАВНА
автоматической remediation
```

После одной ошибки система может:

- дать похожее задание;
- изменить representation;
- оставить текущую Difficulty;
- провести delayed check;
- продолжить обучение.

Решение зависит от истории.

---

# 20. Повторяющиеся ошибки

При повторяющихся ошибках Adaptive Engine может:

```text
lower difficulty
change representation
repeat concept
check prerequisite
activate remediation
```

---

# 21. Review

Review используется для проверки устойчивости ранее освоенного Skill.

Review необходим, потому что:

> правильный ответ сейчас не гарантирует долговременное знание.

Поэтому ранее освоенные Skills периодически возвращаются в задания.

---

# 22. Due Review

Если Skill достиг состояния, при котором требуется повторная проверка, он становится:

```text
DUE_REVIEW
```

Однако Review не должен перебивать:

```text
Critical Remediation
Required Prerequisite
```

---

# 23. Reinforce

Reinforce используется, когда Skill уже освоен частично или рабоче, но ещё требуется закрепление.

Примеры:

```text
Mastery 2
Mastery 3
```

может привести к:

```text
REINFORCE
```

---

# 24. Advance

Advance означает переход к следующему допустимому Skill.

Advance разрешён только если:

- текущий Skill достаточно освоен;
- необходимые prerequisites выполнены;
- нет критической проблемы;
- нет обязательного remediation;
- нет более важного recent-error recovery.

---

# 25. Более важная проблема всегда имеет приоритет

Например:

```text
Skill A
Mastery 5
→ готов к Advance

Skill B
Mastery 2
→ требуется Reinforce
```

Если Skill B является обязательной предпосылкой для дальнейшего развития, Adaptive Engine выбирает B.

---

# 26. Variety

После выполнения педагогических требований система может использовать:

```text
Interleaving
Context Variety
```

для предотвращения монотонности.

Variety не должна нарушать:

- prerequisites;
- remediation;
- review;
- reinforcement;
- текущую педагогическую необходимость.

---

# 27. Interleaving

Interleaving означает чередование разных Skills.

Например:

```text
ADD
ADD
SUB
ADD
MUL
ADD
SUB
```

вместо:

```text
ADD
ADD
ADD
ADD
ADD
```

---

# 28. Context Variety

Один Skill может появляться в разных игровых контекстах:

```text
BATTLE
QUEST
SHOP
EXPLORATION
PUZZLE
NPC
```

Например:

```text
ADD_CROSS_TEN
```

может использоваться:

```text
в бою
в магазине
в квесте
в задаче на поиск
```

---

# 29. Context Variety не является Math Decision

Adaptive Engine выбирает математический контекст в рамках допустимых вариантов.

Game Engine решает конкретное визуальное и игровое оформление.

---

# 30. Difficulty Selection

Difficulty выбирается **после определения Skill и Mode**.

То есть:

```text
Skill
  ↓
Mode
  ↓
Difficulty
```

а не:

```text
Difficulty
  ↓
Skill
```

---

# 31. Difficulty Selection

Adaptive Engine учитывает:

- Mastery;
- recent errors;
- current Skill;
- Mode;
- историю Difficulty;
- устойчивость результатов;
- representation;
- предыдущие попытки.

---

# 32. Difficulty ≠ Mastery

Mastery:

```text
состояние знаний ребёнка
```

Difficulty:

```text
характеристика текущего задания
```

Они связаны, но не являются одним параметром.

Например:

```text
Mastery = 4
```

не означает:

```text
Difficulty = 4
```

---

# 33. Difficulty Progression

Пример:

```text
Mastery 2
→ Difficulty 1–2

Mastery 3
→ Difficulty 2–3

Mastery 4
→ Difficulty 3–4
```

Это концептуальная модель.

Фактическое решение принимается Adaptive Policy.

---

# 34. Difficulty после Skill + Mode

Правильная последовательность:

```text
Determine Skill
        ↓
Determine Mode
        ↓
Determine Difficulty
        ↓
Task Generator
```

---

# 35. Adaptive Mode

Adaptive Engine может выбирать Mode:

```text
LEARN
REINFORCE
REVIEW
REMEDIATION
ADVANCE
```

Mode является частью Adaptive Decision.

---

# 36. Task Blueprint

После принятия решения Adaptive Engine создаёт:

```text
TaskBlueprint
```

Например:

```text
TaskBlueprint
├── skillId
├── difficulty
├── mode
├── contextType
├── taskConstraints
└── allowedRepresentations
```

---

# 37. Adaptive Decision

Каноническая структура:

```text
AdaptiveDecision
├── skillId
├── difficulty
├── mode
├── contextType
├── constraints
└── reason
```

---

# 38. Reason

`reason` необходим для:

- отладки;
- QA;
- аналитики;
- объяснения решения;
- воспроизводимости.

Пример:

```text
reason:
RECENT_ERROR_RECOVERY
```

или:

```text
reason:
REQUIRED_PREREQUISITE
```

---

# 39. Reason не является текстом для ребёнка

Adaptive reason — техническое поле.

Например:

```text
REQUIRED_PREREQUISITE
```

не должно напрямую отображаться пользователю.

---

# 40. Adaptive Policy

Правила должны быть конфигурируемыми.

```text
AdaptivePolicy
├── hardConstraints
├── criticalRemediationRules
├── prerequisiteRules
├── recentErrorRules
├── reviewRules
├── reinforceRules
├── advanceRules
├── interleavingRules
├── contextRules
├── difficultyRules
└── fallbackRules
```

---

# 41. Rule-Based MVP

MVP использует:

```text
Rule-Based Adaptive Engine
```

Преимущества:

- объяснимость;
- предсказуемость;
- тестируемость;
- offline execution;
- простая отладка;
- отсутствие необходимости в большом объёме данных.

---

# 42. ML-ready Architecture

Архитектура должна позволять в будущем использовать:

```text
Machine Learning
```

без изменения публичного контракта.

Например:

```text
Adaptive Engine
      │
      ├── Rule Policy
      │
      └── Future ML Policy
```

---

# 43. ML не должен владеть Mastery

Даже при использовании ML:

```text
Math Engine
    ↓
Mastery
```

остаётся authoritative.

ML/Adaptive Policy использует состояние, но не становится источником математической истины.

---

# 44. Adaptive Engine и Content Model

Content Model предоставляет:

```text
Skills
Prerequisites
Task Types
Templates
Representations
Constraints
Difficulty Profiles
```

Adaptive Engine использует эти данные.

Content Model не выбирает следующий Skill.

---

# 45. Adaptive Engine и Task Generator

```text
Adaptive Engine
       ↓
Task Blueprint
       ↓
Task Generator
       ↓
Task Instance
```

Task Generator не должен самостоятельно менять Adaptive Decision.

---

# 46. Если генерация невозможна

Если Task Generator сообщает:

```text
generation unavailable
```

Adaptive Engine может выполнить fallback.

Например:

```text
другой Template
другая Representation
другая Difficulty
другой допустимый Context
```

но не должен нарушать педагогические ограничения.

---

# 47. Fallback

Fallback применяется только если стандартное решение невозможно.

Порядок:

```text
1. другой допустимый Template
2. другая Representation
3. другая Difficulty
4. другой допустимый Context
5. другой допустимый Task Type
6. другой Skill с учётом prerequisites
```

Fallback не должен обходить Critical Remediation или Required Prerequisite.

---

# 48. Недопустимый fallback

Нельзя:

```text
Generation failed
→ случайный Skill
```

или:

```text
Generation failed
→ пропустить prerequisite
```

---

# 49. Deterministic Mode

Для QA Adaptive Engine должен поддерживать:

```text
deterministic mode
```

с фиксированным:

```text
seed
```

---

# 50. Воспроизводимость

При одинаковых:

```text
Learner State
Skill State
Adaptive Policy
Content Version
Seed
```

решение должно быть воспроизводимым.

Это необходимо для:

- unit tests;
- regression tests;
- bug reproduction;
- QA;
- анализа проблем.

---

# 51. Adaptive History

Рекомендуется хранить историю решений:

```text
AdaptiveDecisionHistory
├── timestamp
├── skillId
├── difficulty
├── mode
├── contextType
├── reason
├── policyVersion
└── contentVersion
```

---

# 52. Policy Version

Каждое решение должно быть связано с версией Adaptive Policy.

Например:

```text
policyVersion = 1.0.0
```

Это позволяет понять, почему система приняла определённое решение.

---

# 53. Пример

Состояние:

```text
ADD_BASIC       Mastery 5
ADD_CROSS_TEN   Mastery 3
SUB_BASIC       Mastery 4
MUL_EQUAL_GROUPS Mastery 1
```

При этом:

```text
MUL_TABLE
requires
MUL_EQUAL_GROUPS
```

Adaptive Engine не должен сразу переходить к `MUL_TABLE`.

Сначала:

```text
MUL_EQUAL_GROUPS
```

---

# 54. Пример recent error

```text
ADD_CROSS_TEN
Mastery = 4

Последние попытки:
✓
✗
✗
```

Adaptive Engine может выбрать:

```text
Skill:
ADD_CROSS_TEN

Mode:
REMEDIATION

Difficulty:
ниже предыдущей

Representation:
NUMBER_LINE
```

---

# 55. Пример Review

```text
SUB_BASIC
Mastery = 5
Review = due
```

Если нет более важной проблемы:

```text
Skill:
SUB_BASIC

Mode:
REVIEW
```

---

# 56. Пример Advance

```text
ADD_BASIC
Mastery = 5
Prerequisites = satisfied
No recent critical errors
Review = not due
```

Adaptive Engine может выбрать:

```text
next eligible Skill
```

---

# 57. Приоритеты — окончательная модель

```text
                 HARD CONSTRAINTS
                        │
                        ▼
              CRITICAL REMEDIATION
                        │
                        ▼
              REQUIRED PREREQUISITE
                        │
                        ▼
              RECENT ERROR RECOVERY
                        │
                        ▼
                  DUE REVIEW
                        │
                        ▼
                   REINFORCE
                        │
                        ▼
                    ADVANCE
                        │
                        ▼
                    VARIETY
                        │
                        ▼
              DIFFICULTY SELECTION
                        │
                        ▼
                    FALLBACK
```

Hard Constraints являются предварительным фильтром допустимости.

---

# 58. Почему такой порядок

Логика:

```text
сначала устранить критическую проблему
        ↓
затем обеспечить prerequisites
        ↓
затем отработать свежие проблемы
        ↓
затем выполнить необходимый review
        ↓
затем закрепить
        ↓
затем продвигаться
        ↓
затем добавить разнообразие
```

---

# 59. Variety как tie-breaker

Если несколько решений имеют одинаковый педагогический приоритет:

```text
Interleaving
```

и:

```text
Context Variety
```

могут использоваться как tie-breaker.

Они не должны переопределять педагогическую необходимость.

---

# 60. Adaptive Decision Lifecycle

```text
Input State
    ↓
Hard Constraints
    ↓
Candidate Skills
    ↓
Priority Evaluation
    ↓
Skill Selection
    ↓
Mode Selection
    ↓
Difficulty Selection
    ↓
Context Selection
    ↓
Constraint Assembly
    ↓
Adaptive Decision
    ↓
Task Blueprint
```

---

# 61. Candidate Skills

Math Engine / Content Model могут предоставить набор допустимых Skills.

Adaptive Engine фильтрует и выбирает среди них.

---

# 62. Skill Selection

Skill выбирается на основании:

```text
Mastery
Prerequisites
Recent Errors
Review State
Learning History
Current Progress
Adaptive Policy
```

---

# 63. Mode Selection

После определения причины выбора определяется Mode:

```text
CRITICAL_REMEDIATION
→ REMEDIATION

REQUIRED_PREREQUISITE
→ LEARN / REINFORCE

RECENT_ERROR
→ REINFORCE / REMEDIATION

DUE_REVIEW
→ REVIEW

REINFORCE
→ REINFORCE

ADVANCE
→ ADVANCE
```

---

# 64. Difficulty Selection

После Skill + Mode:

```text
Skill
↓
Mode
↓
Difficulty
```

Difficulty учитывает текущую способность ребёнка выполнить выбранный тип задачи.

---

# 65. Context Selection

Context выбирается из допустимых вариантов:

```text
BATTLE
QUEST
SHOP
EXPLORATION
PUZZLE
NPC
```

Приоритет:

```text
pedagogical need
>
context variety
```

---

# 66. Representation Selection

Adaptive Engine может выбрать предпочтительную Representation:

```text
NUMERIC
TEXT
VISUAL_GROUPS
NUMBER_LINE
TABLE
SYMBOLIC
```

Особенно важно при remediation.

Например:

```text
ошибка в абстрактной записи
→ visual representation
```

---

# 67. Adaptive Engine не генерирует значения

Неправильно:

```text
Adaptive Engine
→ 27 + 18
```

Правильно:

```text
Adaptive Engine
→ ADD_CROSS_TEN
→ Difficulty 2
→ DIRECT_CALCULATION
→ REQUIRE_CARRY
```

Затем:

```text
Task Generator
→ 27 + 18
```

---

# 68. Adaptive Engine не вычисляет ответ

Неправильно:

```text
Adaptive Engine
→ 27 + 18 = 45
```

Правильно:

```text
Adaptive Engine
→ определить Skill и параметры задания
```

Математическую истину определяет Math Engine.

---

# 69. Adaptive Engine и Mastery

Критическая граница:

```text
Math Engine
      ↓
evaluates attempt
      ↓
updates Mastery
      ↓
Adaptive Engine reads Mastery
```

Нельзя:

```text
Adaptive Engine
      ↓
updates Mastery
```

---

# 70. Adaptive Engine и Rewards

Reward System получает результат обучения отдельно.

Adaptive Engine не решает:

```text
сколько Coins
сколько XP
какой Item
какая награда
```

---

# 71. Adaptive Engine и Level System

Level System определяет:

```text
RPG progression
```

Adaptive Engine определяет:

```text
mathematical progression
```

Они связаны, но независимы.

---

# 72. Adaptive Engine и Game Engine

Game Engine сообщает контекст:

```text
battle
quest
shop
exploration
```

Adaptive Engine выбирает допустимый математический сценарий.

Game Engine не должен самостоятельно выбирать математическую Difficulty.

---

# 73. Mathematical Progression

Математическая progression основана на:

```text
Skills
Prerequisites
Mastery
Adaptive Decisions
```

а не на:

```text
RPG Level
Age
Time Played
Coins
```

---

# 74. RPG Level ≠ Mathematical Level

Ребёнок может иметь:

```text
RPG Level = 10
```

и:

```text
Skill A Mastery = 2
Skill B Mastery = 5
```

Adaptive Engine работает с математическим состоянием, а не просто с RPG Level.

---

# 75. Age ≠ Adaptive Level

Возраст не должен напрямую определять:

```text
Skill
Difficulty
Mode
```

Например:

```text
8 years old
→ ADD_CROSS_TEN
```

является неправильной логикой.

---

# 76. Diagnostic Mode

В будущем Adaptive Engine может поддерживать:

```text
DIAGNOSTIC
```

для быстрого определения текущего уровня.

Диагностика может позволить пропускать уже освоенные Skills.

---

# 77. Delayed Stability Check

Даже если Skill показал высокий Mastery:

```text
не считать его навсегда закрытым
```

Adaptive Engine периодически возвращает Skill для проверки устойчивости.

---

# 78. Ошибки не уничтожают Mastery

Adaptive Engine не должен трактовать одну ошибку как полную потерю навыка.

Например:

```text
Mastery 4
→ одна ошибка
→ Mastery не обязательно становится 0
```

Конкретная корректировка определяется `08_MASTERY_SYSTEM`.

---

# 79. Adaptive Safety

Adaptive Engine должен предотвращать:

- бесконечное повторение одного задания;
- зацикливание на одном Skill;
- бесконечное снижение Difficulty;
- обход prerequisites;
- чрезмерное усложнение;
- повторение одной Representation;
- однообразный Context.

---

# 80. Anti-loop

Необходимо обнаруживать ситуации:

```text
A
→ B
→ A
→ B
→ A
```

если такой цикл не предусмотрен Adaptive Policy.

---

# 81. Minimum Variety

При отсутствии педагогической необходимости повторять одно и то же:

```text
Skill
Task Type
Representation
Context
```

следует избегать непосредственного повторения.

---

# 82. Repetition

Повторение допустимо, если оно оправдано:

```text
remediation
reinforcement
recent error recovery
review
```

---

# 83. Неуспешная попытка

После неудачи Adaptive Engine может:

```text
оставить Skill
снизить Difficulty
изменить Representation
повторить Skill
проверить prerequisite
```

Решение зависит от политики и истории.

---

# 84. Успешная попытка

После успешной попытки Adaptive Engine анализирует:

```text
updated Mastery
history
review state
next prerequisites
```

и решает:

```text
repeat
reinforce
review
advance
```

---

# 85. Multiple Attempts

Несколько успешных попыток подряд не обязательно означают немедленный Advance.

Система должна учитывать:

```text
stability
variety
different contexts
different representations
delayed checks
```

---

# 86. Memorization vs Understanding

Adaptive Engine должен помогать отличать:

```text
memorized pattern
```

от:

```text
actual understanding
```

Для этого используются:

- разные Task Types;
- разные Representations;
- разные Contexts;
- interleaving;
- delayed review;
- изменяющиеся параметры.

---

# 87. Пример

Ребёнок решил:

```text
3 × 4
4 × 3
```

Это ещё не означает полное понимание умножения.

Следующая проверка может использовать:

```text
equal groups
```

или:

```text
word problem
```

или:

```text
missing factor
```

---

# 88. Adaptive Explainability

Каждое решение должно иметь техническую причину.

Например:

```text
reason = DUE_REVIEW
```

или:

```text
reason = REQUIRED_PREREQUISITE
```

или:

```text
reason = RECENT_ERROR_RECOVERY
```

Это критично для QA.

---

# 89. Adaptive Metrics

Система должна позволять измерять:

```text
selection frequency
remediation frequency
review frequency
advance frequency
difficulty changes
fallback frequency
context distribution
representation distribution
```

---

# 90. QA Metrics

Особенно важны:

```text
% решений с fallback
% циклов
% repeated tasks
% premature advance
% missed remediation
% prerequisite violations
```

---

# 91. Unit Testing

Adaptive Engine должен иметь тесты на:

```text
priority order
prerequisites
critical remediation
recent errors
review
reinforce
advance
interleaving
context variety
difficulty
fallback
determinism
anti-loop
```

---

# 92. Priority Test

Пример:

```text
Skill A → Advance
Skill B → Critical Remediation
```

Ожидается:

```text
Skill B
```

---

# 93. Prerequisite Test

```text
MUL_TABLE
requires
MUL_EQUAL_GROUPS

MUL_EQUAL_GROUPS
not mastered
```

Ожидается:

```text
MUL_EQUAL_GROUPS
```

---

# 94. Review Test

```text
Skill A
Mastery = 5
Review = due
No critical problems
```

Ожидается:

```text
Mode = REVIEW
```

---

# 95. Difficulty Test

При одинаковом:

```text
Skill
Mode
```

изменение состояния ребёнка может изменить:

```text
Difficulty
```

но не должно менять математическую принадлежность Skill.

---

# 96. Deterministic Test

При одинаковых:

```text
state
policy
content version
seed
```

ожидается одинаковый:

```text
AdaptiveDecision
```

---

# 97. Fallback Test

Если выбранный Template недоступен:

```text
Adaptive Engine
```

должен выбрать допустимый fallback, а не случайный Skill.

---

# 98. Adaptive Contract

Минимальный контракт:

```text
AdaptiveDecision
{
    skillId,
    difficulty,
    mode,
    contextType,
    constraints,
    reason
}
```

---

# 99. Responsibility Matrix

| Функция | Math Engine | Mastery System | Adaptive Engine | Task Generator |
|---|---:|---:|---:|---:|
| Mathematical truth | ✓ | | | |
| Answer validation | ✓ | | | |
| Mastery calculation | | ✓ | | |
| Mastery update | | ✓ | | |
| Skill selection | | | ✓ | |
| Mode selection | | | ✓ | |
| Difficulty selection | | | ✓ | |
| Context selection | | | ✓ | |
| Exact task generation | | | | ✓ |
| Exact numbers | | | | ✓ |
| Distractors | | | | ✓ |

---

# 100. Финальная архитектура

```text
┌───────────────────────────────┐
│          MATH ENGINE          │
│                               │
│ mathematical truth            │
│ answer evaluation             │
│ skill graph                   │
│ learner mathematical state   │
└───────────────┬───────────────┘
                │
                │ Mastery / history / graph
                ▼
┌───────────────────────────────┐
│       ADAPTIVE ENGINE         │
│                               │
│ skill selection               │
│ mode selection                │
│ review                        │
│ remediation                   │
│ reinforcement                │
│ advance                       │
│ interleaving                  │
│ context variety               │
│ difficulty selection          │
└───────────────┬───────────────┘
                │
                │ AdaptiveDecision
                ▼
┌───────────────────────────────┐
│        TASK GENERATOR         │
│                               │
│ concrete values               │
│ expressions                   │
│ text                          │
│ options                       │
│ task instance                 │
└───────────────────────────────┘
```

---

# 101. Каноническая модель

```text
                    HARD CONSTRAINTS
                           │
                           ▼
                  CRITICAL REMEDIATION
                           │
                           ▼
                  REQUIRED PREREQUISITE
                           │
                           ▼
                  RECENT ERROR RECOVERY
                           │
                           ▼
                       DUE REVIEW
                           │
                           ▼
                        REINFORCE
                           │
                           ▼
                         ADVANCE
                           │
                           ▼
                        VARIETY
                           │
                           ▼
                  DIFFICULTY SELECTION
                           │
                           ▼
                        FALLBACK
```

---

# 102. Главный архитектурный принцип

> **Adaptive Engine не обучает ребёнка напрямую. Он принимает решение о следующем математическом шаге на основании состояния знаний ребёнка.**

---

# 103. Ключевое разделение

```text
CURRICULUM
    ↓
что существует в математике

MATH ENGINE
    ↓
что математически истинно
и что ребёнок показал

MASTERY SYSTEM
    ↓
как изменяется состояние освоения

ADAPTIVE ENGINE
    ↓
что ребёнку дать дальше

TASK GENERATOR
    ↓
как превратить решение в конкретное задание

GAME ENGINE
    ↓
как превратить результат в игровой опыт
```

---

# 104. Финальный runtime pipeline

```text
                    PLAYER
                       │
                       ▼
                  Task Instance
                       │
                       ▼
                 Player Answer
                       │
                       ▼
                  MATH ENGINE
                       │
              ┌────────┴────────┐
              ▼                 ▼
        Evaluation         Skill State
                                  │
                                  ▼
                         MASTERY SYSTEM
                                  │
                                  ▼
                         Updated Mastery
                                  │
                                  ▼
                         ADAPTIVE ENGINE
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
                 Skill                         Mode
                    │                           │
                    └─────────────┬─────────────┘
                                  ▼
                             Difficulty
                                  │
                                  ▼
                              Context
                                  │
                                  ▼
                         Adaptive Decision
                                  │
                                  ▼
                          Task Blueprint
                                  │
                                  ▼
                         TASK GENERATOR
                                  │
                                  ▼
                           Task Instance
                                  │
                                  ▼
                            VALIDATION
                                  │
                                  ▼
                            GAME ENGINE
                                  │
                                  ▼
                              PLAYER
```

---

# 105. Статус документа

**`09_ADAPTIVE_ENGINE.md v1.1 — APPROVED`**

Документ является нормативным для реализации Adaptive Engine.

Ключевые архитектурные правила:

```text
1. Hard Constraints — фильтр допустимости.
2. Critical Remediation имеет высший педагогический приоритет.
3. Required Prerequisite важнее продвижения.
4. Recent Error Recovery важнее обычного Advance.
5. Review не должен перебивать серьёзную текущую проблему.
6. Reinforce выполняется перед Advance, если требуется закрепление.
7. Variety используется только после педагогических требований.
8. Difficulty выбирается после Skill + Mode.
9. Adaptive Engine не изменяет Mastery.
10. Adaptive Engine не генерирует конкретные задания.
11. Adaptive Engine не является источником математической истины.
12. Все решения должны быть объяснимыми и тестируемыми.
13. MVP — rule-based.
14. Архитектура должна быть ML-ready.
15. Adaptive Decision должна быть воспроизводимой при deterministic mode.
```

---

# 106. Ключевая формула

> **Math Engine определяет состояние знаний. Mastery System определяет изменение состояния освоения. Adaptive Engine выбирает следующий шаг. Task Generator создаёт конкретное задание.**