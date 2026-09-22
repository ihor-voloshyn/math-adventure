# 18_QUEST_SYSTEM.md

**Проект:** Math Adventure  
**Документ:** Quest System — система квестов  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

## 1. Назначение

Quest System связывает игровой мир, персонажей, исследование, математику и RPG progression в понятные приключения.

Квест отвечает на вопрос:

> Что герой делает в мире и зачем?

Квест не определяет математическую правильность, Mastery или Adaptive Policy.

## 2. Основной принцип

Quest → Game Activity → Math Interaction where appropriate → Result → Game Progression.

Квесты должны создавать мотивацию для прохождения математических заданий, а не выглядеть как набор учебных упражнений.

## 3. Типы квестов MVP

Минимально:
- STORY;
- ADVENTURE;
- EXPLORATION;
- COLLECTION;
- COMBAT;
- DISCOVERY;
- HOME/TERRITORY.

Не требуется сложное branching quest tree в первом MVP.

## 4. Структура Quest

QuestDefinition:
- id;
- titleKey;
- descriptionKey;
- type;
- prerequisites;
- objectives;
- rewards;
- availableContexts;
- storyStage;
- recommendedRpgLevel;
- metadata.

QuestInstance:
- questId;
- playerId;
- state;
- objectivesProgress;
- startedAt;
- completedAt;
- sessionId.

## 5. Состояния

Минимально:
- LOCKED;
- AVAILABLE;
- ACTIVE;
- COMPLETED;
- FAILED;
- ABANDONED.

FAILED не должен автоматически уничтожать подтверждённый прогресс.

## 6. Objectives

Цель должна быть игровой:

- поговорить с NPC;
- добраться до места;
- победить врага;
- найти предмет;
- исследовать область;
- решить математическую задачу в подходящем контексте;
- вернуть предмет;
- улучшить дом;
- открыть территорию.

Один квест может иметь несколько целей.

## 7. Математика в квесте

Квест не выбирает математический Skill напрямую.

Правильно:

Quest → Game Context → Adaptive Engine → Math Task.

Квест может ограничить допустимый контекст или тип действия, но не заменяет Adaptive Policy.

## 8. Математическая ошибка

Ошибка в математической задаче влияет на соответствующее игровое действие по Combat/Gameplay rules.

Она не должна автоматически проваливать весь квест.

## 9. Повтор квеста

Повторяемый квест может существовать, но награды должны быть ограничены Game Progression/Economy rules.

Нельзя использовать повторение лёгкого квеста как бесконечный источник XP/Coins.

## 10. Quest Rewards

Reward System определяет:
- XP;
- Coins;
- Loot;
- Items;
- unlocks.

Quest System только описывает допустимый reward source.

## 11. Story Quests

Story Quest продвигает основную историю.

MVP должен иметь понятную первую сюжетную цепочку:
- знакомство с героем;
- дом;
- первая проблема;
- деревня;
- первый выход в лес;
- первый серьёзный конфликт;
- раннее упоминание дракона;
- возвращение домой.

## 12. Adventure Quests

Adventure Quests дают самостоятельные приключения:
- исследование;
- помощь NPC;
- поиск;
- бой;
- возвращение.

Они не обязаны менять основную историю.

## 13. Exploration Quests

Exploration Quest мотивирует увидеть новую часть мира:
- найти landmark;
- открыть путь;
- исследовать участок леса;
- найти скрытый объект.

## 14. Collection Quests

Collection Quest использует предметы/ресурсы/артефакты.

В MVP коллекционные цели должны быть простыми и не требовать сложного inventory management.

## 15. Home/Territory Quests

Могут:
- открыть участок;
- улучшить дом;
- получить decoration;
- вернуть предмет домой;
- познакомить с новым NPC.

## 16. Quest Hub

Главными точками выдачи квестов являются:
- village NPCs;
- home NPCs;
- story interactions;
- discovery triggers.

Не нужен отдельный «список заданий» как единственный способ играть.

## 17. Quest Marker

MVP должен использовать понятные визуальные маркеры:
- доступен;
- активен;
- цель рядом;
- цель далеко;
- завершён.

Не перегружать экран.

## 18. Quest Navigation

В MVP игрок должен понимать:
- куда идти;
- что искать;
- что сделать дальше.

Не требуется сложная GPS-подобная навигация.

Можно использовать:
- compass;
- directional marker;
- map marker;
- world-space indicator.

## 19. Quest Flow

```
AVAILABLE
   ↓
START
   ↓
OBJECTIVES
   ↓
GAMEPLAY
   ↓
COMPLETION CHECK
   ↓
REWARD
   ↓
STORY / WORLD UPDATE
```

## 20. Persistence

Quest state сохраняется локально и должен переживать:
- restart;
- load;
- переходы;
- offline play.

## 21. Atomic Completion

Завершение квеста и подтверждение его reward должны быть атомарно связаны.

Нельзя получить reward дважды из-за:
- повторного открытия экрана;
- reconnect;
- restart;
- duplicate event.

Используется eventId/idempotency.

## 22. Defeat

Поражение в бою внутри квеста:
- не уничтожает завершённые цели;
- не сбрасывает дом;
- не сбрасывает Mastery;
- не снимает подтверждённый XP/Coins/Loot;
- может потерять только незакоммичированный текущий результат согласно Game Progression.

## 23. Offline-first

Квесты работают офлайн.

Server sync в будущем подтверждает:
- quest state;
- completion events;
- rewards;
- unlocks.

## 24. RPG Level

RPG Level может ограничивать игровой доступ к квесту.

Он не заменяет математические prerequisites.

Пример:
RPG Level 10 → Quest available.

Но математический Skill остаётся под контролем Math Engine/Mastery/Adaptive.

## 25. Home Integration

Home может быть:
- стартовой точкой;
- точкой сдачи;
- частью objective;
- безопасной зоной;
- местом story update.

## 26. World Integration

Quest может:
- открыть путь;
- открыть NPC;
- открыть область;
- изменить состояние объекта;
- запустить story event.

World/Story остаётся владельцем состояния мира.

## 27. Combat Integration

Combat Quest создаёт Combat Encounter.

Combat System владеет:
- боем;
- HP;
- действиями;
- Combat Result.

Quest System владеет только objective:
- «победить врага»;
- «победить группу»;
- «завершить бой».

## 28. Discovery Integration

Discovery может завершить objective:
- найден landmark;
- открыта новая область;
- обнаружен объект.

## 29. Quest Dependencies

Prerequisites могут быть:
- RPG Level;
- completedQuest;
- worldUnlock;
- homeUnlock;
- itemOwnership;
- storyStage.

Математический Mastery не должен быть напрямую зашит в QuestDefinition как источник истины.

Если требуется математический доступ, квест создаёт подходящий игровой контекст, а Math/Adaptive layer решает математический шаг.

## 30. Quest Branching

MVP использует преимущественно линейные и лёгкие развилки.

Сложные:
- multi-ending;
- persistent moral choices;
- large branching narrative

выходят за MVP.

## 31. Quest Failure

Quest может быть FAILED только по явному игровому условию.

Математическая ошибка сама по себе обычно не является Quest Failure.

## 32. Repeatability

QuestDefinition должен явно указывать:
- one-time;
- repeatable;
- repeatable with limited reward.

Reward policy остаётся внешней.

## 33. Quest Telemetry

Полезно сохранять:
- questId;
- instanceId;
- state transition;
- objectiveId;
- eventId;
- timestamp;
- storyStage;
- rpgLevel;
- context;
- completionReason.

## 34. Child-first UX

Квест должен быть понятен ребёнку без чтения длинной инструкции.

Используются:
- короткий текст;
- персонаж;
- визуальная цель;
- world marker;
- понятный reward preview.

## 35. First Quest Chain

Первая цепочка должна дать полный onboarding loop:

1. Получить первый дом.
2. Познакомиться с питомцем.
3. Поговорить с NPC в деревне.
4. Выполнить первую математическую задачу.
5. Получить первое игровое действие/награду.
6. Исследовать небольшой участок.
7. Выполнить первый бой.
8. Вернуться домой.
9. Получить XP/Coins/Item согласно reward policy.
10. Открыть следующий шаг приключения.

## 36. Quest System не управляет математикой

Запрещено:
- QuestSystem.calculateMastery();
- QuestSystem.checkAnswer();
- QuestSystem.selectMathSkill();
- QuestSystem.grantMathProgress().

## 37. Quest System не управляет RPG progression

Quest System сообщает Game Progression о завершении событий.

Game Progression решает XP/Coins/Loot/unlocks.

## 38. QA

Минимальные тесты:
- availability;
- prerequisite;
- start;
- objective progression;
- completion;
- failure;
- repeatability;
- reward idempotency;
- persistence;
- offline;
- defeat preservation;
- Home integration;
- Combat integration;
- Discovery integration;
- story update.

## 39. Acceptance Criteria

Quest System готов для MVP, если:
1. есть первая линейная цепочка;
2. квесты понятны ребёнку;
3. objectives сохраняются;
4. completion идемпотентен;
5. reward не дублируется;
6. defeat не уничтожает подтверждённый прогресс;
7. квесты работают офлайн;
8. Quest не владеет Mastery/Adaptive/математической проверкой;
9. Quest интегрируется с Home, Combat и World;
10. первый квест приводит ребёнка от дома к первой полноценной игровой активности.

## 40. Основной принцип

> Quest рассказывает ребёнку, **зачем идти в приключение**.

> Game Engine определяет, **что происходит в мире**.

> Adaptive/Math layers определяют, **какая математика подходит сейчас**.

> Game Progression определяет, **что ребёнок получает за результат**.

## 41. Статус

18_QUEST_SYSTEM.md v1.0 — APPROVED.
