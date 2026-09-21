# 07_ADAPTIVE_ENGINE.md

**Проект:** Math Adventure  
**Документ:** Adaptive Engine — адаптивный движок обучения  
**Версия:** 1.1  
**Статус:** APPROVED  
**Дата:** 2026-09-21

---

# 1. Назначение

Adaptive Engine — независимый программный слой Math Adventure, отвечающий за персонализацию математического обучения.

Его основная задача:

> **Определить, что конкретному ребёнку дать следующим и почему.**

Adaptive Engine использует данные Math Engine о:

- навыках;
- Skill Graph;
- Mastery;
- истории попыток;
- ошибках;
- успешных попытках;
- сложности;
- давности проверки.

На основании этих данных Adaptive Engine выбирает следующий математический шаг.

---

# 2. Главный принцип

Архитектурная граница:

```text
Math Engine
    │
    │ состояние математических знаний
    ▼
Adaptive Engine
    │
    │ решение о следующем шаге
    ▼
Task Generator
```

Ключевое правило:

> **Math Engine отвечает на вопрос «что ребёнок знает и что математически произошло?».**

> **Adaptive Engine отвечает на вопрос «что ребёнку дать дальше?».**

---

# 3. Adaptive Engine НЕ является Math Engine

Adaptive Engine не должен:

- вычислять математические ответы;
- проверять арифметику;
- определять математическую правильность;
- изменять математические правила;
- создавать конкретные математические выражения;
- хранить Curriculum как источник истины;
- самостоятельно изменять Mastery;
- управлять боем;
- управлять наградами;
- управлять персонажем;
- управлять игровым миром.

---

# 4. Ответственность Adaptive Engine

Adaptive Engine отвечает за:

- выбор Skill;
- выбор Difficulty;
- выбор режима работы с навыком;
- Reinforce;
- Advance;
- Review;
- Remediation;
- Interleaving;
- повторную проверку;
- управление математической последовательностью;
- баланс нового и уже изученного материала;
- выбор математического контекста/формата в рамках игровых ограничений;
- формирование следующего шага обучения.

---

# 5. Основной цикл

```text
Получить Learner State
        ↓
Получить Skill Graph
        ↓
Получить доступные Skills
        ↓
Применить Hard Constraints
        ↓
Определить приоритет педагогической ситуации
        ↓
Выбрать Skill
        ↓
Выбрать Mode
        ↓
Выбрать Difficulty
        ↓
Выбрать Context / Format
        ↓
Проверить ограничения
        ↓
Передать Task Generator
```

---

# 6. Источники данных

Adaptive Engine получает данные преимущественно от Math Engine.

Минимально:

```text
LearnerState
SkillState
SkillGraph
MathAttemptHistory
```

Дополнительно:

```text
GameContext
AvailableContexts
QuestContext
BattleContext
```

---

# 7. Learner State

Adaptive Engine не должен создавать собственную независимую копию математического состояния ребёнка.

Источник истины:

```text
Math Engine
```

Adaptive Engine получает снимок или представление состояния:

```text
LearnerState
    skills
    mastery
    history
    recentPerformance
    reviewCandidates
```

---

# 8. Skill Selection

Основная функция Adaptive Engine:

```text
selectNextSkill()
```

Она должна определить:

> какой математический навык сейчас наиболее целесообразно дать ребёнку.

Выбор не должен основываться только на:

```text
минимальный Mastery → выбрать его
```

Необходимо учитывать несколько факторов и прежде всего их **приоритет**.

---

# 9. Факторы выбора Skill

Минимально:

```text
Mastery
Prerequisites
Recent Performance
Difficulty History
Time Since Review
Error Pattern
Skill Importance
Current Learning Mode
Interleaving
Game Context
```

Однако эти факторы не равнозначны.

Adaptive Engine использует **иерархию правил**, а не простое суммирование всех сигналов.

---

# 10. Канонический порядок правил адаптации

Порядок правил является архитектурным контрактом Adaptive Engine.

```text
1. HARD CONSTRAINTS
          ↓
2. CRITICAL REMEDIATION
          ↓
3. REQUIRED PREREQUISITE
          ↓
4. RECENT ERROR RECOVERY
          ↓
5. DUE REVIEW
          ↓
6. REINFORCE
          ↓
7. ADVANCE
          ↓
8. INTERLEAVING
          ↓
9. CONTEXT VARIETY
          ↓
10. DIFFICULTY SELECTION
          ↓
11. FALLBACK
```

При этом уровни имеют разную природу.

### Hard Constraints

Это не педагогический приоритет, а предварительный фильтр.

Они определяют:

> **что вообще разрешено выбрать.**

### Педагогические приоритеты

Основная иерархия:

```text
Critical Remediation
        >
Required Prerequisite
        >
Recent Error Recovery
        >
Due Review
        >
Reinforce
        >
Advance
        >
Variety
```

Главный принцип:

> **Более важная педагогическая интервенция всегда имеет приоритет над простой возможностью продолжить прогрессию или добавить разнообразие.**

---

# 11. Hard Constraints

Перед расчётом педагогического приоритета Adaptive Engine сначала исключает недопустимые варианты.

Например:

```text
Skill blocked by prerequisite
Difficulty unavailable
Context unavailable
Content technically unavailable
Recently forbidden repetition
```

Такие кандидаты не участвуют в дальнейшем выборе.

```text
All Skills
    ↓
Hard Constraints
    ↓
Eligible Skills
```

Hard Constraints не должны рассматриваться как причина выбора навыка.

Они определяют множество допустимых кандидатов.

---

# 12. Critical Remediation

Critical Remediation имеет высший педагогический приоритет.

Используется, когда обнаружена ситуация, требующая немедленного вмешательства.

Например:

```text
критический prerequisite gap
+
устойчивые ошибки
+
невозможность продолжать текущую цепочку
```

Тогда:

```text
Critical Remediation
        ↓
выбор корректирующего Skill
```

Critical Remediation может временно прервать обычную progression.

---

# 13. Required Prerequisite

Если текущая цель требует prerequisite, который недостаточно освоен:

```text
A requires B

B недостаточно освоен
```

Adaptive Engine должен предпочесть:

```text
B
```

вместо продолжения:

```text
A
```

Это является обязательным педагогическим ограничением прогрессии.

---

# 14. Recent Error Recovery

После critical remediation и prerequisite следует реакция на недавние ошибки.

Пример:

```text
Skill A
Correct
Correct
Error
Error
```

Adaptive Engine может:

- повторить Skill;
- уменьшить Difficulty;
- изменить представление;
- изменить Context;
- проверить prerequisite;
- перейти к Remediation.

Недавние ошибки имеют больший приоритет, чем обычное продвижение вперёд.

---

# 15. Due Review

Если критических проблем нет, Adaptive Engine проверяет Review-кандидатов.

Например:

```text
Skill A
Mastery = 5
Last Review = давно
```

может стать:

```text
REVIEW
```

Однако Review не должен прерывать Critical Remediation, Required Prerequisite или существенное восстановление после недавних ошибок.

---

# 16. REINFORCE

Если нет более приоритетной проблемы, система рассматривает закрепление.

Reinforce означает:

> закрепить уже изучаемый навык.

Используется, когда:

- навык ещё формируется;
- результаты нестабильны;
- требуется дополнительное подтверждение;
- Mastery недостаточно для перехода дальше.

Пример:

```text
ADD_CROSS_TEN
Mastery = 2

→ REINFORCE
```

---

# 17. ADVANCE

Если навык достаточно устойчив и нет более приоритетной причины для вмешательства:

```text
ADVANCE
```

может означать:

- переход к более сложному варианту текущего Skill;
- переход к следующему Skill;
- продолжение текущей progression.

Например:

```text
ADD_BASIC
Mastery = sufficient
Prerequisites satisfied

→ ADD_CROSS_TEN
```

---

# 18. Interleaving

Interleaving используется после определения основных педагогических потребностей.

Он не должен переопределять:

```text
Critical Remediation
Required Prerequisite
Recent Error Recovery
Due Review
```

если они действительно необходимы.

Interleaving помогает выбирать между несколькими допустимыми вариантами.

Например:

```text
ADD
ADD
SUB
ADD
MUL
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

# 19. Context Variety

Разнообразие контекста является ещё более низким приоритетом, чем педагогическая потребность.

Если:

```text
Battle
Battle
Battle
```

но для ребёнка необходим именно данный Skill и Context допустим, Variety не должна заставлять систему выбирать неподходящий материал.

При наличии нескольких равноценных вариантов можно предпочесть:

```text
Shop
Quest
Puzzle
```

для разнообразия.

---

# 20. Difficulty Selection

Difficulty выбирается **после определения Skill и Mode**.

Это принципиально.

Правильный порядок:

```text
WHAT:
Skill + Mode

↓

HOW HARD:
Difficulty

↓

WHERE:
Context / Format
```

Adaptive Engine не должен сначала выбирать:

```text
Difficulty 4
```

а затем искать подходящий Skill.

---

# 21. Difficulty

Difficulty и Skill являются разными сущностями.

```text
Skill:
ADD_BASIC

Difficulty:
1
2
3
4
5
```

Один Skill может иметь несколько уровней сложности.

---

# 22. Выбор Difficulty

Adaptive Engine определяет:

```text
какой уровень сложности нужен сейчас
```

Task Generator затем создаёт конкретную задачу соответствующей сложности.

Adaptive Engine не создаёт сами числа.

---

# 23. Difficulty не является отдельным педагогическим приоритетом

Difficulty не конкурирует напрямую с:

```text
Remediation
Prerequisite
Review
Reinforce
Advance
```

Сначала определяется педагогическая цель:

```text
REMEDIATION
REINFORCE
ADVANCE
REVIEW
```

затем выбирается подходящая Difficulty.

---

# 24. Пример

Система определила:

```text
Skill = SUB_CROSS_TEN
Mode = REMEDIATION
```

Только после этого:

```text
Difficulty = 2
```

А не:

```text
Difficulty = 4
→ найти любой подходящий Skill
```

---

# 25. Постепенное изменение Difficulty

Недопустимо без достаточных оснований:

```text
Difficulty 1
        ↓
Difficulty 5
```

Переход должен быть постепенным.

Например:

```text
1 → 2 → 3
```

или:

```text
3 → 2
```

при ухудшении результатов.

---

# 26. Skill Selection ≠ Difficulty Selection

Это два разных решения.

Сначала:

```text
Какой Skill?
```

Затем:

```text
Какой Mode?
```

Затем:

```text
Какая Difficulty?
```

Например:

```text
Skill = ADD_CROSS_TEN
Mode = REINFORCE
Difficulty = 3
```

---

# 27. Rule-Based MVP

Первая версия Adaptive Engine должна быть прозрачной.

Базовый алгоритм:

```text
1. Filter by Hard Constraints

2. Check Critical Remediation

3. Check Required Prerequisites

4. Check Recent Error Recovery

5. Check Due Review

6. Check Reinforce

7. Check Advance

8. Apply Interleaving as tie-breaker

9. Apply Context Variety as tie-breaker

10. Select Difficulty

11. Apply Fallback if necessary
```

Этот порядок является нормативным для MVP.

---

# 28. Псевдокод основного алгоритма

```text
candidates = getEligibleCandidates()

if criticalRemediationExists(candidates):
    candidate = selectCriticalRemediation(candidates)

else if requiredPrerequisiteExists(candidates):
    candidate = selectRequiredPrerequisite(candidates)

else if recentErrorRecoveryExists(candidates):
    candidate = selectRecentErrorRecovery(candidates)

else if reviewIsDue(candidates):
    candidate = selectReview(candidates)

else if reinforceCandidateExists(candidates):
    candidate = selectReinforce(candidates)

else if advanceCandidateExists(candidates):
    candidate = selectAdvance(candidates)

else:
    candidate = selectByVariety(candidates)

mode = determineMode(candidate)

difficulty = selectDifficulty(candidate, mode)

context = selectContext(candidate)

validateDecision()

if invalid:
    fallback()
```

---

# 29. Основной принцип приоритета

Adaptive Engine должен следовать правилу:

```text
педагогическая необходимость
        >
обычная progression
        >
разнообразие
```

Иными словами:

> **Variety не должна побеждать необходимость исправить проблему.**

И:

> **Advance не должна побеждать необходимость закрепить или восстановить навык.**

---

# 30. Один сигнал не должен автоматически менять режим

Например:

```text
1 ошибка
```

не означает автоматически:

```text
REMediation
```

Adaptive Engine должен учитывать:

- количество ошибок;
- их последовательность;
- Difficulty;
- Context;
- предыдущую историю;
- Mastery;
- prerequisite.

---

# 31. Серия ошибок

Пример:

```text
Error
Error
Error
```

Adaptive Engine может:

1. снизить Difficulty;
2. изменить представление;
3. изменить Context;
4. дать дополнительную математическую подсказку;
5. проверить prerequisite;
6. перейти в Remediation.

Конкретное действие зависит от политики.

---

# 32. Серия правильных ответов

Пример:

```text
Correct
Correct
Correct
Correct
```

Adaptive Engine может:

- повысить Difficulty;
- изменить Context;
- проверить другой формат;
- перейти к следующему Skill;
- запланировать Review позднее.

Количество правильных ответов само по себе не является единственным условием перехода.

---

# 33. Четыре основных режима

Adaptive Engine использует:

```text
REINFORCE
ADVANCE
REVIEW
REMEDIATION
```

Mode выбирается после определения наиболее приоритетной педагогической ситуации.

---

# 34. REINFORCE

Reinforce:

> закрепить уже изучаемый навык.

Пример:

```text
ADD_CROSS_TEN
Mastery = 2

→ REINFORCE
```

---

# 35. ADVANCE

Advance:

> перейти к следующему этапу освоения.

Например:

```text
ADD_BASIC
Mastery = 4

→ ADD_CROSS_TEN
```

или:

```text
ADD_CROSS_TEN
Mastery = 4

→ higher difficulty
```

---

# 36. REVIEW

Review:

> повторно проверить ранее освоенный Skill.

Например:

```text
ADD_BASIC
Mastery = 5
review due

→ REVIEW
```

---

# 37. REMEDIATION

Remediation:

> временно вернуться к более доступному материалу, необходимому для восстановления понимания.

Например:

```text
DIVISION
      ↓
устойчивые ошибки
      ↓
Remediation
      ↓
MUL_EQUAL_GROUPS
```

---

# 38. Ошибки в prerequisite

Если ребёнок хорошо решает:

```text
DIVISION
```

но начинает устойчиво ошибаться в:

```text
MULTIPLICATION
```

Adaptive Engine может временно вернуть:

```text
MULTIPLICATION
```

как Remediation.

---

# 39. Необходимость prerequisite

Если:

```text
A requires B
```

и:

```text
B недостаточно освоен
```

то:

```text
B
```

имеет приоритет над обычным:

```text
Advance → A
```

---

# 40. Critical Remediation против обычного Reinforce

Если одновременно присутствуют:

```text
Critical Remediation
+
Reinforce
```

выбирается:

```text
Critical Remediation
```

Потому что восстановление критического prerequisite важнее обычного закрепления.

---

# 41. Recent Error Recovery против Review

Если одновременно:

```text
Recent Error Recovery
+
Due Review
```

приоритет имеет:

```text
Recent Error Recovery
```

Review может быть перенесён.

---

# 42. Review против Advance

Если:

```text
Review due
+
Advance candidate
```

Review может получить приоритет.

Advance не должен автоматически отменять обязательную повторную проверку устойчивости ранее изученного материала.

---

# 43. Reinforce против Advance

Если:

```text
Skill unstable
```

то:

```text
REINFORCE
```

имеет приоритет над:

```text
ADVANCE
```

---

# 44. Advance против Variety

Если:

```text
только один педагогически подходящий Skill
```

то Adaptive Engine не должен выбирать другой Skill только ради разнообразия.

Variety применяется, когда существует несколько равноценных вариантов.

---

# 45. Несколько одинаково подходящих навыков

Если после применения всех основных правил остаётся несколько кандидатов:

```text
ADD_CROSS_TEN
SUB_CROSS_TEN
LOGIC_SEQUENCE
```

можно использовать:

- Interleaving;
- Context Variety;
- round-robin;
- ротацию;
- контролируемую случайность.

---

# 46. Deterministic Mode

Для QA и диагностики Adaptive Engine должен поддерживать детерминированный режим.

Например:

```text
seed = 12345
```

При одинаковом:

```text
LearnerState
+
GameContext
+
Configuration
+
Seed
```

результат должен быть воспроизводимым.

---

# 47. Randomization

Случайность может использоваться для:

- выбора между равноприоритетными Skills;
- вариации контекста;
- предотвращения повторения;
- разнообразия заданий.

Но случайность не должна нарушать:

```text
Hard Constraints
Prerequisites
Pedagogical Priority
Difficulty Limits
```

---

# 48. Запрет на «рандомное обучение»

Неверно:

```text
random skill
random difficulty
random task
```

без анализа состояния ребёнка.

Правильно:

```text
Learner State
      ↓
Eligibility
      ↓
Priority
      ↓
Controlled Variation
```

---

# 49. Контекст

Один Skill может использоваться в разных игровых ситуациях.

Например:

```text
ADD_BASIC
 ├── BATTLE
 ├── SHOP
 ├── QUEST
 ├── PUZZLE
 ├── EXPLORATION
 └── CONSTRUCTION
```

Adaptive Engine может выбрать контекст из разрешённых Game Engine вариантов.

---

# 50. Context Selection

Context выбирается после:

```text
Skill
Mode
Difficulty
```

и с учётом доступных игровых условий.

Контекст не должен менять математическую цель.

---

# 51. Game Context

Game Engine сообщает:

```text
где сейчас находится ребёнок
```

Например:

```text
BATTLE
SHOP
QUEST
```

Adaptive Engine адаптирует математику к этому контексту.

---

# 52. Контекст не должен определять Skill

Неверно:

```text
SHOP
   ↓
только MONEY
```

Магазин может использовать:

```text
ADDITION
SUBTRACTION
MULTIPLICATION
MONEY
WORD_PROBLEMS
LOGIC
```

Конкретный выбор делает Adaptive Engine.

---

# 53. Interleaving

Interleaving означает чередование разных навыков.

Например:

```text
ADD
ADD
SUB
ADD
MUL
SUB
ADD
```

Interleaving является механизмом разнообразия и tie-breaker, а не механизмом, способным отменить более высокий педагогический приоритет.

---

# 54. Баланс нового и знакомого

Adaptive Engine балансирует:

```text
NEW
+
PRACTICE
+
REVIEW
```

Но баланс не должен нарушать установленную иерархию.

Например:

```text
critical problem
```

не должен откладываться ради поддержания заданной доли:

```text
NEW / PRACTICE / REVIEW
```

---

# 55. Неизученный навык

Если:

```text
Mastery = 0 / UNKNOWN
```

Adaptive Engine может выбрать Skill для первичного знакомства, если prerequisites доступны.

Это обычно относится к:

```text
ADVANCE / initial learning
```

но может быть переопределено более приоритетной ситуацией.

---

# 56. Cold Start

Для нового ребёнка Adaptive Engine не должен предполагать:

```text
8 лет = определённый математический уровень
```

Возраст не используется как прямой показатель математического Mastery.

---

# 57. Диагностический старт

Для нового ребёнка возможен:

```text
Diagnostic Mode
```

Adaptive Engine может запросить проверку разных Skills.

Цель:

```text
найти известный уровень
+
найти пробелы
+
найти границу освоенных Skills
```

---

# 58. Пропуск освоенного материала

Если диагностика показывает высокий уровень:

```text
ADD_BASIC
Mastery ≈ high
```

Adaptive Engine может не заставлять ребёнка проходить весь базовый материал повторно.

Но система должна позже выполнить подтверждающую проверку.

---

# 59. Delayed Confirmation

Например:

```text
Diagnostic
    ↓
ADD_BASIC looks mastered
    ↓
Advance
    ↓
через некоторое время
    ↓
Review ADD_BASIC
```

Пропуск не означает:

```text
навсегда mastered
```

---

# 60. Review Scheduling

Adaptive Engine должен уметь создавать будущие Review-кандидаты.

Минимальные параметры:

```text
skillId
lastReviewedAt
mastery
reviewPriority
```

MVP может использовать простые правила.

Например:

```text
Mastery 4 → периодический Review
Mastery 5 → более редкий Review
```

---

# 61. Ошибки после высокого Mastery

Пример:

```text
ADD_BASIC
Mastery = 5
```

Ребёнок ошибся.

Adaptive Engine не должен немедленно:

```text
Mastery = 0
```

Вместо этого:

```text
ошибка
 ↓
дополнительное наблюдение
 ↓
при необходимости Review
 ↓
при устойчивых ошибках Recovery / Remediation
```

Изменение Mastery выполняет Math Engine.

---

# 62. Несколько кандидатов

После Hard Constraints может остаться:

```text
ADD_CROSS_TEN
SUB_CROSS_TEN
MUL_EQUAL_GROUPS
LOGIC_SEQUENCE
```

Adaptive Engine применяет правила сверху вниз.

Он не должен выбирать кандидата только потому, что у него минимальный Mastery.

---

# 63. Adaptive Priority Model

Внутреннее представление может быть:

```text
Candidate
├── eligible
├── criticalRemediation
├── prerequisiteRequired
├── recentErrorRecovery
├── reviewDue
├── reinforce
├── advance
├── interleavingValue
└── contextVarietyValue
```

Это не обязательно должна быть единая числовая формула.

---

# 64. Приоритеты не обязаны быть одной математической формулой

В MVP предпочтительнее:

```text
ordered rules
```

чем непрозрачная сумма:

```text
score =
0.23 * mastery
+
0.17 * review
+
...
```

Это делает поведение системы:

- объяснимым;
- тестируемым;
- воспроизводимым;
- управляемым.

---

# 65. Skill-specific Policy

В будущем:

```text
Skill
    ↓
Adaptive Policy Override
```

Например:

```text
MUL_TABLE
review more frequently
```

а:

```text
NUMBER_LINE
review less frequently
```

Skill-specific policy не должна нарушать общий порядок критических правил.

---

# 66. Не превращать систему в набор исключений

Нельзя получить:

```text
if skill == A
if skill == B
if skill == C
if skill == D
...
```

Вместо этого:

```text
SkillPolicy
```

должна содержать декларативные параметры.

---

# 67. Adaptive Engine и Task Generator

### Adaptive Engine

Решает:

```text
Skill = ADD_CROSS_TEN
Mode = REINFORCE
Difficulty = 3
Context = SHOP
```

### Task Generator

Создаёт:

```text
У героя 27 монет.
Он получил ещё 18.
Сколько стало?
```

Adaptive Engine не должен генерировать конкретные числа.

---

# 68. Adaptive Engine и Math Engine

### Math Engine

```text
Mastery = 3
SkillGraph
History
Correct / Incorrect
```

### Adaptive Engine

```text
что делать дальше?
```

Например:

```text
Math Engine:
ADD_CROSS_TEN mastery = 3

Adaptive Engine:
REINFORCE
Difficulty = 2
Context = QUEST
```

---

# 69. Полный цикл

```text
GAME ENGINE
     │
     │ request math
     ▼
ADAPTIVE ENGINE
     │
     │ eligibility
     │ priority
     │ Skill
     │ Mode
     │ Difficulty
     │ Context
     ▼
TASK GENERATOR
     │
     │ concrete task
     ▼
PLAYER
     │
     │ answer
     ▼
MATH ENGINE
     │
     │ evaluate
     │ update Mastery
     ▼
ADAPTIVE ENGINE
     │
     │ next decision
     ▼
GAME ENGINE
```

---

# 70. Adaptive Engine не должен менять Mastery

Критическое правило:

```text
AdaptiveEngine
       ✕
Mastery Update
```

Правильно:

```text
Player Answer
      ↓
Math Engine
      ↓
Mastery Update
      ↓
Adaptive Engine reads new state
```

---

# 71. Adaptive Engine не должен проверять ответ

Неверно:

```text
Adaptive Engine:
if answer == 15
```

Проверка принадлежит Math Engine.

Adaptive Engine получает:

```text
correct = true / false
```

и использует этот факт для следующего решения.

---

# 72. Adaptive Engine не должен генерировать математические выражения

Неверно:

```text
Adaptive Engine:
generate 17 + 28
```

Правильно:

```text
Adaptive Engine:
Skill = ADD_CROSS_TEN
Difficulty = 3

Task Generator:
17 + 28
```

---

# 73. Fallback

Fallback используется только после невозможности выполнить нормальный путь.

```text
Normal Decision
      ↓
не удалось
      ↓
Fallback
```

Fallback имеет самый низкий приоритет.

---

# 74. Безопасный fallback

Если оптимальное решение невозможно:

```text
Adaptive Decision unavailable
        ↓
последний подтверждённый eligible Skill
        ↓
безопасная Difficulty
        ↓
доступный Context
```

Игра не должна ломаться из-за сбоя адаптации.

---

# 75. Fallback не должен ломать prerequisites

Даже в fallback нельзя выбрать:

```text
Skill A
```

если обязательный prerequisite:

```text
Skill B
```

не выполнен.

Hard Constraints применяются и к fallback.

---

# 76. Offline-first

Adaptive Engine должен работать локально.

В MVP интернет не требуется для:

- выбора Skill;
- выбора Difficulty;
- адаптации;
- Review;
- Remediation;
- прохождения игры.

---

# 77. Будущий сервер

В будущем:

```text
Client Adaptive Engine
        ↓
Server
        ↓
Long-term Learner Model
```

Сервер сможет использовать накопленную историю для более сложных моделей.

Но игровой клиент должен сохранять возможность локальной работы.

---

# 78. ML в будущем

Архитектура должна позволять заменить:

```text
Rule-Based Policy
```

на:

```text
ML-based Policy
```

без изменения:

- Math Engine;
- Task Generator;
- Game Engine;
- математических Skill ID.

---

# 79. Возможные будущие модели

Архитектура может поддержать:

- Bayesian Knowledge Tracing;
- Item Response Theory;
- Deep Knowledge Tracing;
- contextual bandits;
- reinforcement learning;
- гибридные модели.

В MVP они не требуются.

---

# 80. Правило безопасности ML

Даже при появлении ML система должна иметь:

```text
Hard Constraints
```

которые модель не может нарушить.

Например:

```text
не нарушать prerequisites
не выдавать недопустимую Difficulty
не повторять запрещённый контент
не использовать недоступный Context
```

Кроме того, ML не должен обходить установленную педагогическую иерархию без явно определённой политики.

---

# 81. Quality Guard

Перед передачей решения Task Generator Adaptive Engine должен проверить:

```text
Skill eligible?
Difficulty allowed?
Prerequisites valid?
Context allowed?
Recent repetition acceptable?
```

Если нет:

```text
recalculate decision
```

Quality Guard не должен выбирать новый Skill произвольно.

---

# 82. Adaptive Decision Pipeline

```text
Learner State
      ↓
Candidate Skills
      ↓
Eligibility Filter
      ↓
Pedagogical Priority
      ↓
Skill + Mode
      ↓
Difficulty Selection
      ↓
Context Selection
      ↓
Constraint Validation
      ↓
AdaptiveDecision
```

---

# 83. Candidate Skills

Candidate Skills формируются на основании:

- Curriculum;
- Skill Graph;
- Learner State.

Adaptive Engine не должен выбирать навык, которого нет в Curriculum.

---

# 84. Eligibility Filter

Фильтр исключает:

- заблокированные prerequisites;
- недоступные игровые контексты;
- запрещённую Difficulty;
- недавно чрезмерно повторявшиеся задания;
- технически недоступные варианты.

---

# 85. Priority Calculation

После фильтрации остаётся:

```text
Candidate Set
```

Но приоритет должен определяться не простым рейтингом.

Система последовательно проверяет:

```text
Critical Remediation
↓
Required Prerequisite
↓
Recent Error Recovery
↓
Due Review
↓
Reinforce
↓
Advance
↓
Variety
```

---

# 86. Внутренние scores

Внутренние значения допустимы для выбора:

```text
ADD_CROSS_TEN   0.82
SUB_CROSS_TEN   0.71
MUL_GROUPS      0.55
LOGIC_SEQUENCE  0.42
```

Но такой score не должен автоматически заменять нормативный порядок правил.

Он может использоваться внутри одной категории кандидатов.

---

# 87. Не использовать рейтинг навыков как игровой показатель

Приоритет:

```text
0.82
```

не должен означать:

```text
ребёнок хуже знает навык
```

Это только внутренний показатель Adaptive Engine.

---

# 88. Ограничение частоты

Adaptive Engine должен контролировать частоту:

```text
одного Skill
одной формы
одного Context
одной Difficulty
```

Но ограничение повторов не должно мешать обязательной Remediation.

Если необходимый Skill должен быть повторён:

```text
Pedagogical Priority
```

имеет приоритет над обычным Variety Limit.

---

# 89. Контекстная ротация

Если один Skill используется много раз:

```text
Battle
Battle
Battle
```

Adaptive Engine может при возможности выбрать:

```text
Shop
Quest
Puzzle
```

если это не противоречит:

```text
Critical Remediation
Prerequisite
Error Recovery
Review
Reinforce
```

---

# 90. Формат ответа

Math Adventure поддерживает:

```text
Free Numeric Input
Multiple Choice
Selection
```

Adaptive Engine может учитывать доступные форматы.

Для нечисловых математических ответов MVP использует **выбор из предложенных вариантов**, а не произвольный текстовый ввод.

---

# 91. Обучающий режим

Adaptive Engine не должен превращать каждое задание в проверку.

При низком Mastery он может предпочесть:

```text
простое представление
+
подсказка
+
меньшая Difficulty
```

При высоком:

```text
вариативное представление
+
более сложный контекст
+
Review
```

---

# 92. Hints

В MVP математические подсказки:

```text
бесплатны
```

Adaptive Engine может инициировать их доступность через Task/Session constraints.

Но содержание подсказки создаёт не Adaptive Engine, а соответствующий математический/контентный слой.

---

# 93. Адаптация после подсказки

Например:

```text
Correct + Hint
```

Adaptive Engine может решить:

```text
ещё одно задание того же Skill
```

вместо немедленного Advance.

Это не означает автоматического снижения Mastery.

---

# 94. Поведение после SKIPPED

Если ребёнок пропустил задачу:

```text
SKIPPED
```

Adaptive Engine не должен считать это:

```text
INCORRECT
```

Он может:

- отложить Skill;
- изменить Context;
- попробовать позже;
- дать более понятное представление.

---

# 95. Игровой прогресс не должен управлять математикой напрямую

Game Engine может сообщить:

```text
playerLevel = 4
```

но Adaptive Engine не должен считать:

```text
RPG Level 4 = Math Level 4
```

RPG и математическая прогрессия являются разными системами.

---

# 96. Математическая сложность не равна RPG Level

Например:

```text
RPG Level = 8
```

не означает автоматически:

```text
Math Difficulty = 8
```

Adaptive Engine основывается на математическом состоянии ребёнка.

---

# 97. Связь с наградой

Adaptive Engine может определить математический уровень сложности.

Но он не выдаёт:

```text
Coins
XP
Items
```

Game Engine использует результат и игровые правила для формирования награды.

---

# 98. Сложность и дополнительная награда

В проекте допускается:

```text
более сложная задача
        ↓
потенциально большая игровая награда
```

Но это регулируется игровыми правилами.

В MVP прямой выбор ребёнком Difficulty не является обязательным.

---

# 99. Ограничение выбора сложности

Для MVP:

```text
Adaptive Engine
      ↓
выбирает Difficulty
```

Ребёнок не должен иметь отдельный экран:

```text
Easy / Normal / Hard
```

для ручного управления математической адаптацией.

---

# 100. Более высокая сложность после определённого прогресса

Переход к заданиям, предназначенным для более высокой награды, может становиться доступным только после достижения определённого уровня прогресса.

Например:

```text
RPG / progression gate
        ↓
higher challenge available
```

Но Adaptive Engine всё равно решает, подходит ли такая задача конкретному ребёнку.

---

# 101. Adaptive Constraints

Перед Task Generator Adaptive Engine формирует ограничения.

Например:

```text
skill = ADD_CROSS_TEN
mode = REINFORCE
difficulty = 3
context = SHOP
allowMultipleChoice = true
allowFreeNumericInput = true
avoidRecentPattern = true
```

---

# 102. Adaptive Decision

Концептуальная структура:

```text
AdaptiveDecision
    skillId
    difficulty
    mode
    contextType
    constraints
    reason
```

Например:

```text
skillId = SUB_CROSS_TEN
difficulty = 2
mode = REMEDIATION
contextType = QUEST
```

---

# 103. Reason

Для диагностики и Parent Dashboard полезно сохранять внутреннюю причину решения.

Например:

```text
reason =
    "critical_remediation"
```

или:

```text
reason =
    "required_prerequisite"
```

или:

```text
reason =
    "recent_error_recovery"
```

или:

```text
reason =
    "review_due"
```

Причина не обязана показываться ребёнку.

---

# 104. Explainability

Adaptive Engine должен быть объяснимым.

Для любого решения желательно иметь:

```text
Почему выбран этот Skill?
Почему такой Mode?
Почему такая Difficulty?
Почему Review?
Почему Remediation?
```

Особенно важно для:

- QA;
- поддержки;
- Parent Dashboard;
- анализа ошибок адаптации.

---

# 105. Adaptive Policy

Политика должна быть конфигурационной.

Например:

```text
mastery thresholds
review intervals
error thresholds
difficulty step
interleaving rules
repetition limits
context rules
fallback rules
```

Сам порядок приоритетов является архитектурно определённым, но параметры внутри правил должны быть конфигурируемыми.

---

# 106. Конфигурация

Пример:

```text
AdaptiveConfig

reinforceMasteryMax = 3
advanceMasteryMin = 4
reviewMastery = 4..5

errorThreshold = configurable
recentRepeatLimit = configurable
difficultyStep = 1
```

Это пример структуры.

Фактические значения должны быть откалиброваны после тестирования.

---

# 107. Почему thresholds не должны быть окончательными

Например:

```text
Mastery >= 4 → Advance
```

не обязательно оптимально для каждого навыка.

Для:

```text
MUL_TABLE
```

и:

```text
LOGIC_SEQUENCE
```

могут потребоваться разные критерии.

Архитектура должна позволять Skill-specific policy.

---

# 108. Adaptive Engine и Game Session

Adaptive Engine работает внутри игровой сессии.

Сессия может содержать:

```text
Skill A
Skill A
Skill B
Skill A
Skill C
Review A
```

Но критическая педагогическая проблема должна иметь возможность временно изменить обычный порядок сессии.

---

# 109. Session Context

Adaptive Engine может учитывать:

```text
sessionId
recentSkills
recentContexts
recentDifficulties
recentErrors
recentSuccesses
```

Это помогает избежать:

```text
ADD_BASIC
ADD_BASIC
ADD_BASIC
ADD_BASIC
```

без необходимости.

---

# 110. Ограничение повторов

Adaptive Engine должен иметь защиту от чрезмерного повторения одного и того же:

```text
skill
context
difficulty
task format
```

Однако:

> **Ограничения Variety не могут запрещать необходимую педагогическую интервенцию.**

---

# 111. Variety

Для одного Skill желательно варьировать:

```text
числа
формулировку
визуальное представление
игровой контекст
тип ответа
структуру задачи
```

Task Generator отвечает за конкретную генерацию.

Adaptive Engine отвечает за направление и ограничения разнообразия.

---

# 112. Не наказывать за медленный ответ

В соответствии с Product Requirements:

```text
slow + correct
```

не должно автоматически приводить к:

```text
lower mastery
```

Скорость может собираться как отдельная аналитика, но не является основным критерием математической компетентности.

---

# 113. Прогресс ребёнка

Adaptive Engine не должен стремиться искусственно ускорить ребёнка.

Главная цель:

```text
устойчивое математическое понимание
```

а не:

```text
максимальное количество пройденных Skill за сессию
```

---

# 114. Защита от farming

Adaptive Engine не должен позволять бесконечно получать лёгкие задания ради игровых наград.

Например:

```text
Mastery 5
ADD_BASIC
Difficulty 1
repeat × 100
```

не должно становиться оптимальной стратегией получения наград.

Игровая награда регулируется Game/Economy Systems.

---

# 115. Защита от слишком сложных заданий

Adaptive Engine не должен постоянно выбирать задания выше текущей зоны освоения.

Недопустимо:

```text
Mastery 1
→ Difficulty 5
→ Difficulty 5
→ Difficulty 5
```

если нет специального диагностического режима.

---

# 116. Зона продуктивной сложности

Цель Adaptive Engine:

```text
слишком легко
        ←→
продуктивная сложность
        ←→
слишком сложно
```

Система должна стремиться удерживать ребёнка в зоне, где:

- задача понятна;
- есть вызов;
- возможен успех;
- ошибки дают полезную информацию;
- ребёнок хочет продолжать игру.

---

# 117. Parent Dashboard

Adaptive Engine должен предоставлять данные о логике адаптации.

В будущем родитель может увидеть:

```text
Почему ребёнку снова дали сложение?
→ требуется закрепление

Почему вернулись к умножению?
→ обнаружены повторные ошибки

Почему появилась более сложная задача?
→ навык показал устойчивое владение
```

---

# 118. Не показывать техническую логику ребёнку

Ребёнку не обязательно сообщать:

```text
Mastery = 3
Adaptive Score = 0.82
Review Priority = 0.71
```

Вместо этого:

```text
новое приключение
новая задача
новая способность
```

---

# 119. Телеметрия

Для будущего анализа рекомендуется сохранять:

```text
adaptiveDecisionId
playerId
skillId
difficulty
mode
context
timestamp
decisionReason
candidateCount
```

---

# 120. QA Telemetry

Для QA особенно полезно:

```text
inputStateHash
configurationVersion
adaptivePolicyVersion
randomSeed
decisionReason
selectedSkill
selectedDifficulty
selectedContext
```

Это позволит воспроизвести ошибочное решение.

---

# 121. Версионирование Adaptive Policy

Adaptive Policy должна иметь версию.

Например:

```text
policyVersion = 1.0
```

Если правила изменились:

```text
policyVersion = 1.1
```

История решений должна сохранять использованную версию.

---

# 122. A/B Testing в будущем

Архитектура может поддерживать:

```text
Policy A
Policy B
```

для исследования различных адаптивных стратегий.

Но эксперименты не должны нарушать:

- математическую безопасность;
- prerequisites;
- ограничения Difficulty;
- ограничения Curriculum;
- Hard Constraints.

---

# 123. Тестируемость

Минимальный набор тестов:

```text
Candidate Selection Tests
Eligibility Tests
Priority Order Tests
Prerequisite Tests
Difficulty Tests
Reinforce Tests
Advance Tests
Review Tests
Remediation Tests
Interleaving Tests
Context Variety Tests
Fallback Tests
Deterministic Tests
Configuration Tests
```

---

# 124. Тест: Critical Remediation

```text
Given:
    critical remediation condition exists
    reinforce candidate also exists

When:
    selectNextStep()

Then:
    critical remediation has priority
```

---

# 125. Тест: Required Prerequisite

```text
Given:
    A requires B
    B is insufficient
    no critical remediation exists

When:
    selectNextStep()

Then:
    B has priority over ordinary progression to A
```

---

# 126. Тест: Recent Error Recovery

```text
Given:
    repeated recent errors on Skill A
    review candidate B exists

When:
    selectNextStep()

Then:
    recent error recovery has priority over ordinary review
```

---

# 127. Тест: Review

```text
Given:
    no critical issue
    no required prerequisite
    no significant recent error recovery
    Skill Mastery = 5
    review interval expired

When:
    selectNextStep()

Then:
    Skill can become REVIEW candidate
```

---

# 128. Тест: Reinforce

```text
Given:
    no higher-priority intervention
    Skill = ADD_CROSS_TEN
    Mastery = 2

When:
    selectNextStep()

Then:
    mode = REINFORCE
```

---

# 129. Тест: Advance

```text
Given:
    no higher-priority intervention
    ADD_BASIC Mastery is sufficient
    ADD_CROSS_TEN prerequisites are satisfied

When:
    selectNextStep()

Then:
    ADD_CROSS_TEN may become a candidate
```

---

# 130. Тест: единичная ошибка

```text
Given:
    Mastery = 5
    one incorrect answer

When:
    selectNextStep()

Then:
    do not automatically reset mastery
    do not automatically force remediation
```

---

# 131. Тест: Interleaving

```text
Given:
    several candidates have equal pedagogical priority

Then:
    Interleaving may influence selection
```

Но:

```text
Interleaving
```

не может отменить:

```text
Critical Remediation
Prerequisite
Recent Error Recovery
```

---

# 132. Тест: Context Variety

```text
Given:
    several candidates are pedagogically equivalent
    multiple contexts are available

Then:
    Context Variety may influence selection
```

---

# 133. Тест: Difficulty

```text
Given:
    Skill = ADD_CROSS_TEN
    Mode = REINFORCE

When:
    selectDifficulty()

Then:
    Difficulty is selected for ADD_CROSS_TEN
    within REINFORCE requirements
```

Нельзя:

```text
select Difficulty first
→ then choose Skill
```

---

# 134. Тест: повторение

```text
Given:
    same Skill + same Context
    repeated several times

Then:
    Adaptive Engine should consider another eligible context
```

если это не противоречит более высокому педагогическому приоритету.

---

# 135. Тест: deterministic mode

```text
Given:
    same LearnerState
    same Config
    same Seed

When:
    selectNextStep()

Then:
    same AdaptiveDecision
```

---

# 136. Тест: fallback

```text
Given:
    preferred adaptive decision unavailable

Then:
    select safe eligible fallback
    do not violate prerequisites
    do not crash game
```

---

# 137. Запрещённые архитектурные решения

### 1. Adaptive Engine изменяет Mastery

Неверно:

```text
AdaptiveEngine.mastery = 4
```

---

### 2. Adaptive Engine проверяет математику

Неверно:

```text
AdaptiveEngine.correct(17 + 8)
```

---

### 3. Adaptive Engine создаёт конкретные задания

Неверно:

```text
AdaptiveEngine.generate("17 + 8")
```

---

### 4. Adaptive Engine управляет наградами

Неверно:

```text
AdaptiveEngine.addCoins(50)
```

---

### 5. Adaptive Engine управляет RPG

Неверно:

```text
AdaptiveEngine.levelUpPlayer()
```

---

### 6. Возраст определяет Skill

Неверно:

```text
age = 8
→ mathLevel = 3
```

---

### 7. Рандом вместо адаптации

Неверно:

```text
randomSkill()
```

без проверки состояния ребёнка.

---

### 8. Variety переопределяет педагогическую необходимость

Неверно:

```text
нужно remediation
+
другой Skill просто ради разнообразия
```

Правильно:

```text
педагогическая необходимость
        >
variety
```

---

### 9. Difficulty выбирается раньше Skill

Неверно:

```text
Difficulty = 4
→ найти подходящий Skill
```

Правильно:

```text
Skill + Mode
        ↓
Difficulty
```

---

# 138. Минимальная модель данных

```text
AdaptiveDecision
├── id
├── playerId
├── skillId
├── difficulty
├── mode
├── contextType
├── reason
├── policyVersion
├── seed
└── timestamp
```

---

# 139. Adaptive Policy Model

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

Порядок этих правил соответствует утверждённой иерархии.

---

# 140. Интерфейс Adaptive Engine

Концептуально:

```text
getCandidates(playerId, context)

filterEligible(candidates)

selectPriorityCandidate(candidates)

selectMode(skillId, playerState)

selectDifficulty(skillId, mode, playerState)

selectContext(skillId, availableContexts)

createDecision(playerId, context)
```

Фактический API будет определён в технической архитектуре.

---

# 141. Главный контракт с Math Engine

Adaptive Engine получает:

```text
Skill
SkillGraph
SkillState
MathAttemptHistory
Mastery
```

и возвращает:

```text
AdaptiveDecision
```

---

# 142. Главный контракт с Task Generator

Adaptive Engine передаёт:

```text
skillId
difficulty
mode
context
constraints
```

Task Generator возвращает конкретное задание.

---

# 143. Главный контракт с Game Engine

Game Engine сообщает:

```text
GameContext
AvailableActions
AvailableContexts
```

Adaptive Engine возвращает:

```text
AdaptiveDecision
```

Game Engine использует результат для запуска соответствующего игрового действия.

---

# 144. Полная архитектурная схема

```text
┌─────────────────────────────┐
│       MATH ENGINE           │
│                             │
│ Curriculum                  │
│ Skills                      │
│ Skill Graph                 │
│ Learner State               │
│ Mastery                      │
│ Attempt Evaluation          │
└──────────────┬──────────────┘
               │
               │ mathematical state
               ▼
┌─────────────────────────────┐
│      ADAPTIVE ENGINE        │
│                             │
│ Hard Constraints            │
│ Priority Rules              │
│ Candidate Selection         │
│ Skill Selection             │
│ Mode Selection              │
│ Difficulty Selection        │
│ Reinforce                   │
│ Advance                     │
│ Review                      │
│ Remediation                │
│ Interleaving                │
│ Context Selection           │
│ Adaptive Policy             │
└──────────────┬──────────────┘
               │
               │ AdaptiveDecision
               ▼
┌─────────────────────────────┐
│       TASK GENERATOR        │
│                             │
│ Concrete Numbers            │
│ Expressions                 │
│ Word Problems               │
│ Answer Options              │
│ Variations                  │
└──────────────┬──────────────┘
               │
               │ concrete task
               ▼
┌─────────────────────────────┐
│         GAME ENGINE         │
│                             │
│ Battle                      │
│ Quest                       │
│ Shop                        │
│ Puzzle                      │
│ Exploration                 │
│ World                       │
└─────────────────────────────┘
```

---

# 145. Архитектурная граница

Финальное разделение:

```text
┌─────────────────────────────────────────┐
│ MATH ENGINE                             │
│                                         │
│ Что ребёнок знает?                      │
│ Что математически произошло?            │
│ Насколько освоен Skill?                 │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│ ADAPTIVE ENGINE                         │
│                                         │
│ Что дать ребёнку дальше?                │
│ Какой Skill?                            │
│ Какой Mode?                             │
│ Какая Difficulty?                       │
│ Какой Context?                          │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│ TASK GENERATOR                          │
│                                         │
│ Как выглядит конкретная задача?         │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│ GAME ENGINE                             │
│                                         │
│ Где и зачем ребёнок решает задачу?      │
└─────────────────────────────────────────┘
```

---

# 146. Утверждённая иерархия адаптации

Каноническое правило проекта:

```text
HARD CONSTRAINTS
        ↓
CRITICAL REMEDIATION
        ↓
REQUIRED PREREQUISITE
        ↓
RECENT ERROR RECOVERY
        ↓
DUE REVIEW
        ↓
REINFORCE
        ↓
ADVANCE
        ↓
INTERLEAVING
        ↓
CONTEXT VARIETY
        ↓
DIFFICULTY SELECTION
        ↓
FALLBACK
```

При этом:

```text
Hard Constraints
```

являются фильтром допустимости,

а:

```text
Critical Remediation
>
Prerequisite
>
Recent Error Recovery
>
Review
>
Reinforce
>
Advance
>
Variety
```

являются педагогической иерархией.

Difficulty не выбирает педагогическую цель, а определяется **после Skill + Mode**.

---

# 147. Основной принцип

> **Math Engine понимает состояние математических знаний.**

> **Adaptive Engine принимает педагогическое решение о следующем шаге.**

> **Task Generator превращает решение в конкретное математическое задание.**

> **Game Engine помещает задание в игровой контекст.**

Иерархия Adaptive Engine:

> **Сначала система гарантирует допустимость решения, затем решает наиболее важную педагогическую проблему, после этого выбирает способ её реализации, а разнообразие используется только там, где оно не мешает обучению.**

---

# 148. MVP

В MVP Adaptive Engine должен быть:

- Rule-Based;
- детерминированным там, где это необходимо;
- конфигурируемым;
- объяснимым;
- полностью офлайн-работоспособным;
- независимым от RPG Level;
- независимым от возраста ребёнка;
- тестируемым;
- готовым к будущей ML-модели.

Ключевой порядок MVP:

```text
Eligibility
→ Critical Remediation
→ Prerequisite
→ Recent Error Recovery
→ Review
→ Reinforce
→ Advance
→ Variety
→ Difficulty
→ Fallback
```

---

# 149. Будущее развитие

После MVP возможно добавление:

```text
Rule-Based
    ↓
Improved Rule-Based
    ↓
Statistical Model
    ↓
Hybrid Adaptive Model
    ↓
ML / Personalized Policy
```

При этом внешний контракт:

```text
Learner State
        ↓
AdaptiveDecision
```

должен по возможности сохраняться стабильным.

---

# 150. Статус документа

**`07_ADAPTIVE_ENGINE.md v1.1` — APPROVED**

Документ является архитектурной основой для:

- `08_TASK_GENERATION.md`
- `09_MATH_CONTENT_MODEL.md`
- `10_MATH_QA.md`

Главное правило архитектуры:

> **Curriculum определяет математический мир. Math Engine понимает состояние знаний ребёнка. Adaptive Engine выбирает следующий математический шаг. Task Generator превращает этот шаг в конкретное задание. Game Engine превращает математический результат в игровое действие.**