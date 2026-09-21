# 08_TASK_GENERATION.md

**Проект:** Math Adventure  
**Документ:** Task Generation — генерация математических заданий  
**Версия:** 1.2  
**Статус:** APPROVED  
**Дата:** 2026-09-21

---

# 1. Назначение

Task Generator — программный компонент Math Adventure, отвечающий за превращение решения Adaptive Engine в конкретный экземпляр задания.

Главный принцип:

> **Task Generator создаёт конкретное задание. Validation Layers проверяют его техническую, логическую и математическую корректность. Math Engine является authoritative источником математической истины.**

---

# 2. Архитектурное разделение

В системе существуют четыре основные ответственности:

| Компонент | Ответственность |
|---|---|
| **Math Engine** | Математическая истина |
| **Adaptive Engine** | Что дать ребёнку дальше |
| **Task Generator** | Как создать конкретное задание |
| **Game Engine** | Где и как ребёнок его решает |

Дополнительно существует validation pipeline:

```text
Task Instance
      ↓
Structural Validation
      ↓
Logical Validation
      ↓
Mathematical Validation
      ↓
Validated Task
```

---

# 3. Основной поток

```text
Adaptive Decision
        ↓
Task Blueprint
        ↓
Task Generator
        ↓
Task Instance
        ↓
Structural Validation
        ↓
Logical Validation
        ↓
Math Engine Validation
        ↓
Validated Task
        ↓
Game Engine
```

Если любой обязательный этап не пройден:

```text
Task = INVALID
```

и задание не показывается ребёнку.

---

# 4. Adaptive Decision

Adaptive Engine формирует:

```text
AdaptiveDecision
├── skillId
├── difficulty
├── mode
├── contextType
└── constraints
```

Adaptive Engine определяет:

- Skill;
- Mode;
- Difficulty;
- Context;
- педагогические ограничения;
- допустимые форматы.

Task Generator не изменяет это решение.

---

# 5. Task Blueprint

Task Blueprint описывает, **какое задание необходимо создать**.

```text
TaskBlueprint
├── blueprintId
├── skillId
├── difficulty
├── mode
├── contextType
├── allowedTaskTypes
├── allowedAnswerTypes
└── constraints
```

Пример:

```text
skillId = ADD_CROSS_TEN
difficulty = 3
mode = REINFORCE
contextType = SHOP
allowedTaskTypes = WORD_PROBLEM
```

---

# 6. Task Instance

Task Generator создаёт конкретный:

```text
TaskInstance
```

Например:

```text
27 + 18 = ?
```

или:

```text
У героя было 27 монет.
Он получил ещё 18 монет.
Сколько монет стало?
```

Task Instance ещё не считается полностью проверенным.

---

# 7. Три уровня Validation

В проекте явно разделяются:

```text
1. Structural Validation
2. Logical Validation
3. Mathematical Validation
```

Они отвечают на разные вопросы.

---

# 8. Structural Validation

Structural Validation отвечает на вопрос:

> **«Корректно ли технически сформирован Task Instance?»**

Проверяются:

- обязательные поля;
- типы данных;
- schema;
- допустимые enum;
- наличие Task ID;
- наличие Skill ID;
- корректность Difficulty;
- корректность Answer Type;
- корректность Task Type;
- корректность структуры options;
- корректность localization;
- отсутствие повреждённых данных.

Structural Validation **не определяет математическую правильность**.

---

# 9. Пример Structural Validation

Некорректно:

```text
taskId = null
answerType = UNKNOWN
options = malformed
```

Результат:

```text
STRUCTURALLY_INVALID
```

---

# 10. Logical Validation

Logical Validation отвечает на вопрос:

> **«Имеет ли созданное задание корректную и однозначную внутреннюю логику?»**

Проверяются:

- согласованность данных;
- наличие необходимых данных;
- отсутствие противоречий;
- однозначность условия;
- соответствие вопроса данным;
- возможность получить ответ из предоставленной информации;
- отсутствие лишних конфликтующих условий;
- корректность структуры вариантов;
- уникальность допустимого решения там, где требуется один ответ.

Logical Validation работает с **логикой конкретного Task**, а не определяет математическую истину.

---

# 11. Пример Logical Validation

Задание:

```text
У героя 10 яблок.
Он отдал 3 яблока.
Сколько яблок у него осталось?
```

Логически согласовано:

```text
known:
10
3

relation:
gave away

question:
remaining
```

---

# 12. Пример логически некорректного задания

```text
У героя было 10 яблок.
Он отдал 3 яблока.
Сколько яблок он купил?
```

Математически можно выполнить некоторые вычисления, но вопрос не следует из предоставленной информации.

Результат:

```text
LOGICALLY_INVALID
```

---

# 13. Другой пример Logical Validation

Некорректно:

```text
У героя 10 монет.
У него стало 20 монет.
Сколько монет он получил или потратил?
```

Недостаточно информации для однозначного определения действия.

Результат:

```text
LOGICALLY_INVALID
```

---

# 14. Mathematical Validation

Mathematical Validation отвечает на вопрос:

> **«Является ли математическое содержание задания корректным?»**

Эта проверка выполняется Math Engine.

Math Engine проверяет:

- математическую интерпретацию;
- математические правила;
- математический результат;
- математические отношения;
- соответствие Skill;
- математические ограничения;
- правильный ответ;
- уникальность математического результата;
- допустимость математической структуры.

---

# 15. Math Engine — authoritative source

Только Math Engine является authoritative источником:

```text
mathematical truth
correct mathematical result
mathematical evaluation
```

Task Generator не может переопределить результат Math Engine.

---

# 16. Разница Logical и Mathematical Validation

| Проверка | Вопрос |
|---|---|
| Structural | Правильно ли сформирован объект? |
| Logical | Имеет ли задача внутренне корректную и однозначную логику? |
| Mathematical | Верна ли математика задачи? |

---

# 17. Важное ограничение

Logical Validation **не должна самостоятельно становиться вторым Math Engine**.

Например, Logical Validator не должен самостоятельно решать:

```text
27 + 18 = 45
```

Это ответственность Math Engine.

Logical Validator может проверить:

```text
question requires a numerical result
required operands exist
condition and question are connected
```

а математический результат проверяет Math Engine.

---

# 18. Task Generator и Mathematical Constraints

Task Generator может использовать математические ограничения при генерации.

Например:

```text
DIV_EXACT
```

может требовать:

```text
dividend % divisor = 0
```

Generator пытается создать подходящий экземпляр.

Math Engine окончательно подтверждает математическую корректность.

---

# 19. Task Generator и Logical Constraints

Generator также может использовать логические ограничения.

Например:

```text
requiredInformation
uniqueSolution
noContradictions
questionMustReferenceKnownRelation
```

Generator использует их для создания задания.

Logical Validator окончательно проверяет конкретный Task Instance.

---

# 20. Pipeline Validation

Полный pipeline:

```text
Task Instance
      ↓
┌────────────────────────┐
│ Structural Validation  │
└───────────┬────────────┘
            ↓
┌────────────────────────┐
│ Logical Validation     │
└───────────┬────────────┘
            ↓
┌────────────────────────┐
│ Math Engine Validation │
└───────────┬────────────┘
            ↓
      Validated Task
```

---

# 21. Почему именно такой порядок

Сначала проверяется дешёвая техническая корректность.

Затем логическая целостность.

И только после этого запускается математическая проверка.

```text
Structure
   ↓
Logic
   ↓
Math
```

Это уменьшает количество математических проверок заведомо повреждённых объектов.

---

# 22. Structural Validation Result

```text
StructuralValidationResult
├── status
├── errors
└── warnings
```

Статусы:

```text
VALID
INVALID
```

---

# 23. Logical Validation Result

```text
LogicalValidationResult
├── status
├── errors
├── warnings
├── ambiguityDetected
└── contradictionDetected
```

Статусы:

```text
VALID
INVALID
AMBIGUOUS
CONTRADICTORY
```

---

# 24. Mathematical Validation Result

```text
MathValidationResult
├── status
├── validatedAnswer
├── validatedOptions
├── mathematicalFacts
└── diagnostics
```

Статусы:

```text
VALID
INVALID
```

---

# 25. Validated Task

Итоговый объект:

```text
ValidatedTask
├── taskInstance
├── structuralValidation
├── logicalValidation
├── mathematicalValidation
└── validationVersion
```

Только `ValidatedTask` может передаваться Game Engine.

---

# 26. TaskInstance Contract

```text
TaskInstance
├── taskId
├── blueprintId
├── skillId
├── difficulty
├── mode
├── contextType
├── taskType
├── question
├── mathematicalPayload
├── answerType
├── options
├── hints
├── templateId
├── representationId
├── locale
├── generatorVersion
├── seed
└── metadata
```

---

# 27. Mathematical Payload

`mathematicalPayload` описывает математическую структуру.

Например:

```text
operation = ADD
operand1 = 27
operand2 = 18
```

или:

```text
expression = 27 + 18
```

Task Generator создаёт этот payload.

Math Engine проверяет его математический смысл.

---

# 28. Correct Answer

Task Generator не является authoritative источником `correctAnswer`.

Правильная архитектура:

```text
Task Generator
      ↓
mathematicalPayload
      ↓
Math Engine
      ↓
validatedAnswer
```

---

# 29. Multiple Choice

Generator создаёт:

```text
27 + 18 = ?

A. 35
B. 45
C. 46
D. 55
```

Logical Validation проверяет:

- количество вариантов;
- отсутствие дубликатов;
- соответствие формату;
- отсутствие логических конфликтов.

Math Engine проверяет:

- какой вариант математически правильный;
- что правильный вариант существует;
- что при необходимости существует ровно один правильный вариант.

---

# 30. Word Problems

Word Problem состоит из:

```text
context
knownValues
relations
question
mathematicalPayload
```

Пример:

```text
У героя было 27 монет.
Он получил ещё 18 монет.
Сколько монет стало?
```

Logical Validation проверяет:

```text
known values exist
action exists
question corresponds to action
no contradiction
```

Math Engine проверяет:

```text
27 + 18
```

и математический результат.

---

# 31. Logic Tasks

Для Logic Tasks граница особенно важна.

Logical Validation проверяет:

- целостность логической структуры;
- наличие необходимых условий;
- отсутствие противоречий;
- однозначность;
- корректность структуры вариантов.

Math Engine проверяет математическую часть там, где она присутствует.

---

# 32. Pure Logic

Для задач, которые не содержат математической операции, например:

```text
A выше B.
B выше C.
Кто ниже всех?
```

Logical Validator проверяет структуру задачи.

Для таких задач математическая операция может отсутствовать.

При этом задание всё равно должно иметь определённый Skill и проходить соответствующую проверку доменной корректности.

---

# 33. Sequence

Пример:

```text
2, 4, 6, 8, ?
```

Logical Validation проверяет:

- последовательность существует;
- вопрос относится к последовательности;
- формат ответа соответствует задаче.

Math Engine проверяет математическую закономерность и правильный результат, если данный Skill относится к математической последовательности.

---

# 34. Missing Value

Пример:

```text
□ + 7 = 15
```

Logical Validation:

```text
unknown exists
question asks for unknown
expression is structurally complete
```

Math Engine:

```text
□ = 8
```

---

# 35. Fractions

Logical Validation:

- все необходимые части присутствуют;
- вопрос соответствует представлению;
- нет противоречащих условий.

Math Engine:

- проверяет математическую интерпретацию дроби;
- математические отношения;
- правильный результат.

---

# 36. Money

Logical Validation проверяет:

```text
price exists
quantity exists
payment exists where required
question corresponds to scenario
```

Math Engine проверяет:

```text
price × quantity
change
comparison
budget
```

---

# 37. Time

Logical Validation:

```text
start exists
duration/end exists as required
question corresponds to supplied data
```

Math Engine:

```text
start + duration = end
```

и другие математические отношения.

---

# 38. Localization Validation

Localization является отдельным техническим слоем внутри Structural/Logical Validation.

Проверяется:

- наличие перевода;
- наличие всех placeholders;
- сохранение числовых значений;
- сохранение переменных;
- отсутствие повреждённых строк;
- отсутствие изменения логической структуры.

Математическая истина при этом всё равно проверяется Math Engine.

---

# 39. Logical Validation после Localization

Для текстовых задач желательно проверять логическую целостность уже локализованного текста.

Причина:

```text
Source Task
      ↓
Translation
      ↓
meaning may change
```

Поэтому локализованная версия должна оставаться логически эквивалентной исходной.

---

# 40. Distractors

Task Generator создаёт distractors.

Logical Validation проверяет:

```text
no duplicates
valid option structure
no accidental ambiguity
```

Math Engine проверяет математический статус каждого варианта.

---

# 41. Однозначность

Понятие однозначности разделяется.

### Logical uniqueness

Условие не допускает нескольких различных интерпретаций.

### Mathematical uniqueness

При выбранной математической интерпретации существует требуемый единственный математический результат.

Первое проверяется Logical Validation.

Второе — Math Engine.

---

# 42. Пример различия

Задание:

```text
У Маши 5 яблок.
Она получила ещё 3.
Сколько яблок стало?
```

Логически:

```text
одна интерпретация
```

Математически:

```text
5 + 3 = 8
```

Оба уровня должны пройти.

---

# 43. Логически корректно, математически неверно

Например Generator создал:

```text
У героя было 27 монет.
Он получил ещё 18.
Сколько стало?
```

Logical Validation:

```text
VALID
```

Но если математический payload ошибочно содержит:

```text
27 - 18
```

Math Engine:

```text
INVALID
```

Задание отклоняется.

---

# 44. Математически вычислимо, но логически неверно

Например:

```text
У героя было 27 монет.
Он получил 18 монет.
Сколько монет он потратил?
```

Можно математически выполнить:

```text
27 + 18
```

но вопрос не соответствует описанному событию.

Logical Validation:

```text
INVALID
```

Задача отклоняется до Math Engine.

---

# 45. Generation Retry

При ошибке:

```text
Task Instance
      ↓
Validation Failed
      ↓
Regenerate
```

Generator получает возможность создать новый Instance.

---

# 46. Retry по типу ошибки

### Structural failure

Исправляется генератором.

### Logical failure

Generator создаёт другой экземпляр/структуру.

### Mathematical failure

Generator создаёт новый математический экземпляр.

Math Engine не исправляет Task Generator.

---

# 47. Math Engine не генерирует новую задачу

При:

```text
MathValidation = INVALID
```

Math Engine сообщает причину.

Например:

```text
DIVISION_NOT_EXACT
INVALID_FRACTION
WRONG_SKILL_STRUCTURE
INVALID_RESULT
```

Task Generator должен повторить генерацию.

---

# 48. Validation Error

```text
ValidationError
├── layer
├── code
├── message
├── field
└── diagnostics
```

`layer`:

```text
STRUCTURAL
LOGICAL
MATHEMATICAL
```

---

# 49. Примеры ошибок

```text
STRUCTURAL:
MISSING_TASK_ID
INVALID_ANSWER_TYPE
INVALID_OPTIONS

LOGICAL:
AMBIGUOUS_QUESTION
MISSING_REQUIRED_INFORMATION
CONTRADICTORY_CONDITION
MULTIPLE_LOGICAL_INTERPRETATIONS

MATHEMATICAL:
INVALID_OPERATION
WRONG_RESULT
SKILL_CONSTRAINT_VIOLATION
MULTIPLE_MATHEMATICAL_ANSWERS
INVALID_EXPRESSION
```

---

# 50. Deterministic Generation

Generator поддерживает:

```text
seed
generatorVersion
```

При одинаковых параметрах:

```text
skill
difficulty
constraints
locale
seed
generatorVersion
```

должен создаваться одинаковый Task Instance.

---

# 51. Validation Reproducibility

Для QA необходимо сохранять:

```text
taskId
blueprintId
seed
generatorVersion
validationVersion
```

Это позволяет воспроизвести полный pipeline.

---

# 52. Полный пример

```text
Adaptive Engine
      ↓
ADD_CROSS_TEN
Difficulty 3
Shop
      ↓
Task Blueprint
      ↓
Task Generator
      ↓
Task Instance

"У героя было 27 монет.
Он получил ещё 18.
Сколько стало?"

mathematicalPayload:
27 + 18
```

Далее:

```text
Structural Validation
→ VALID
```

```text
Logical Validation
→ VALID
```

```text
Math Engine
→ VALID
→ validatedAnswer = 45
```

Получаем:

```text
ValidatedTask
```

После этого задача передаётся Game Engine.

---

# 53. Полный пример ошибки

Generator создаёт:

```text
27 + 18
```

но указывает:

```text
question = "Сколько стало?"
```

и математический payload:

```text
27 - 18
```

Structural:

```text
VALID
```

Logical:

```text
VALID
```

Math Engine:

```text
INVALID
```

Причина:

```text
question/payload mathematical meaning mismatch
```

Task не показывается ребёнку.

---

# 54. Другой пример ошибки

Generator создаёт:

```text
У героя было 10 монет.
Он получил 5 монет.
Сколько монет он потратил?
```

Structural:

```text
VALID
```

Logical:

```text
INVALID
```

Math Engine не обязан выполнять полную математическую проверку такого Task, поскольку логическая модель уже признана некорректной.

---

# 55. Fallback

Если выбранный Task Type невозможно создать:

```text
preferred Task Type
        ↓
generation failure
        ↓
allowed fallback Task Type
```

Fallback должен сохранять:

- Skill;
- Difficulty;
- Mode;
- математические ограничения;
- допустимый Context;
- педагогические ограничения.

---

# 56. Fallback не меняет Skill

Нельзя:

```text
ADD_CROSS_TEN
    ↓
generation failed
    ↓
MUL_TABLE
```

если это не было заранее разрешено Adaptive Decision.

---

# 57. Offline

Task Generator и validation pipeline MVP работают локально:

```text
Generation = local
Structural Validation = local
Logical Validation = local
Math Validation = local
```

Интернет не требуется для обычного прохождения игры.

---

# 58. Server-ready

В будущем validation может выполняться:

```text
Client
Server
Client + Server
```

Но серверная версия должна использовать те же концептуальные контракты.

---

# 59. Security

Нельзя доверять данным, пришедшим непосредственно из UI:

```text
correctAnswer
reward
mastery
```

Math Engine является источником математической истины.

---

# 60. Task Generator и Mastery

Task Generator не изменяет:

```text
Mastery
SkillState
AdaptiveState
```

Поток:

```text
Math Engine
      ↓
Learner State
      ↓
Adaptive Engine
      ↓
Task Blueprint
      ↓
Task Generator
```

---

# 61. Task Generator и Rewards

Task Generator не определяет:

```text
Coins
XP
Items
Rewards
```

Reward System является отдельной системой.

---

# 62. Task Generator и Difficulty

Generator реализует переданную Difficulty.

Он не должен самостоятельно менять:

```text
Difficulty = 3
```

на:

```text
Difficulty = 5
```

без соответствующего решения Adaptive Engine или явно разрешённого диапазона в Blueprint.

---

# 63. QA

Минимальные группы тестов:

```text
Structural Validation
Logical Validation
Mathematical Validation
Blueprint Integrity
Skill Integrity
Difficulty Integrity
Task Type
Answer Type
Distractors
Word Problems
Logic Tasks
Localization
Deterministic Generation
Diversity
Duplicate Detection
Fallback
Retry
Security
```

---

# 64. Structural QA

Проверить:

```text
missing fields
invalid enum
invalid schema
malformed options
invalid localization
```

---

# 65. Logical QA

Проверить:

```text
ambiguous task
contradictory conditions
missing information
irrelevant question
multiple interpretations
invalid option structure
```

---

# 66. Mathematical QA

Проверить:

```text
correct result
skill compatibility
mathematical constraints
valid expression
unique mathematical answer
division constraints
fraction constraints
```

---

# 67. Critical Architecture Test

Обязательный тест:

```text
Task Generator
    ↓
incorrect mathematicalPayload
    ↓
Math Engine
    ↓
REJECT
```

Generator не может обойти Math Engine.

---

# 68. Architecture Summary

```text
                   ┌─────────────────────┐
                   │   ADAPTIVE ENGINE   │
                   │                     │
                   │ WHAT?               │
                   │ Skill               │
                   │ Mode                │
                   │ Difficulty          │
                   │ Context             │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │   TASK GENERATOR    │
                   │                     │
                   │ HOW?                │
                   │ Numbers             │
                   │ Structure           │
                   │ Text                │
                   │ Options             │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │  TASK INSTANCE      │
                   └──────────┬──────────┘
                              │
                 ┌────────────┴────────────┐
                 ▼                         ▼
       ┌─────────────────┐       ┌─────────────────┐
       │   STRUCTURAL    │       │     LOGICAL     │
       │   VALIDATION    │       │    VALIDATION   │
       └────────┬────────┘       └────────┬────────┘
                └────────────┬────────────┘
                             ▼
                   ┌─────────────────────┐
                   │    MATH ENGINE      │
                   │                     │
                   │ Mathematical Truth  │
                   │ Validation          │
                   │ Evaluation          │
                   │ Mastery             │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │   VALIDATED TASK    │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │    GAME ENGINE      │
                   └─────────────────────┘
```

---

# 69. Финальное разделение

> **Structural Validation отвечает за техническую целостность Task Instance.**

> **Logical Validation отвечает за внутреннюю логическую корректность и однозначность конкретного задания.**

> **Math Engine отвечает за математическую истину и математическую корректность.**

> **Adaptive Engine отвечает за педагогическое решение.**

> **Task Generator отвечает за создание конкретного экземпляра задания.**

---

# 70. Ключевой принцип

```text
Adaptive Engine
    = WHAT?

Task Generator
    = HOW?

Structural Validation
    = IS THE OBJECT WELL-FORMED?

Logical Validation
    = DOES THE TASK MAKE SENSE?

Math Engine
    = IS THE MATHEMATICS TRUE?

Game Engine
    = HOW DOES THE PLAYER EXPERIENCE IT?
```

---

# 71. Статус

**`08_TASK_GENERATION.md v1.2 — APPROVED`**

Зафиксированная архитектура:

```text
Adaptive Decision
        ↓
Task Blueprint
        ↓
Task Generator
        ↓
Task Instance
        ↓
Structural Validation
        ↓
Logical Validation
        ↓
Math Engine Validation
        ↓
Validated Task
        ↓
Game Engine
```

**Математическая и логическая валидация являются разными ответственностями и не должны объединяться в один Validation Layer.**

**Math Engine является authoritative источником математической истины. Logical Validation проверяет логику конкретного задания, но не заменяет Math Engine.**