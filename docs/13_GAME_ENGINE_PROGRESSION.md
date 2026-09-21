# 13_GAME_ENGINE_PROGRESSION.md

**Проект:** Math Adventure  
**Документ:** Game Engine Progression — игровая прогрессия, награды и атомарность  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-21

---

# 1. Назначение

Документ определяет границу игровой прогрессии между Game Engine, Reward/Economy, RPG Level, XP, Coins, Loot и математической прогрессией.

Главный принцип:

> **Игровое событие порождает игровую награду. Математический ответ может быть условием события, но сам по себе не является источником XP.**

# 2. Независимые системы прогресса

В MVP существуют три независимые шкалы:

| Система | Назначение | Источник |
|---|---|---|
| Mastery | математическое освоение | Math Engine / Mastery System |
| XP | RPG-прогресс | Game Progression |
| Coins | игровая экономика | Reward/Economy |

RPG Level вычисляется из накопленного XP. Ни одна система не подменяет другую.

# 3. Основной pipeline

~~~text
Validated Game Event
        ↓
Eligibility / Repeat Policy
        ↓
Reward Policy
        ↓
XP + Coins + Loot
        ↓
RPG Level Calculation
        ↓
Unlock Calculation
        ↓
Atomic Progression Commit
~~~

# 4. Источники XP

Основные источники:

- завершение квеста;
- победа в бою;
- значимое первое исследование;
- завершение игрового puzzle;
- story milestone;
- milestone дома/территории;
- специальное достижение;
- другое явно зарегистрированное игровое событие.

Математический ответ сам по себе не создаёт XP.

# 5. Бой

~~~text
Math Answer
   ↓
Math Engine validates answer
   ↓
Combat action succeeds
   ↓
Enemy defeated
   ↓
Combat Victory Event
   ↓
XP / Coins / Loot
~~~

XP связан с игровым результатом, а не с каждым арифметическим ответом.

# 6. Повторный контент

Первое значимое прохождение получает полный допустимый reward.

Повтор без улучшения сохранённого результата:

~~~text
XP = 0
~~~

Улучшение сохранённого результата может дать ограниченный дополнительный XP, если это разрешено политикой.

Coins/Loot регулируются отдельной Reward Policy.

# 7. Anti-farming

Система не должна позволять бесконечно получать XP за простой повторяемый цикл.

Repeat Policy учитывает:

- sourceId;
- first completion;
- saved/best result;
- previous reward events;
- improvement;
- repeat limits.

# 8. XP не зависит от скорости

Fast + correct и slow + correct не дают разные XP только из-за времени ответа.

Скорость может быть аналитическим параметром, но не основным источником XP.

# 9. Ошибка математики

Неправильный математический ответ может привести к игровому событию неуспеха.

Но:

- подтверждённый XP не уменьшается;
- RPG Level не снижается;
- Mastery обновляется только Math Engine;
- Game Progression не изменяет Mastery.

# 10. Math Hint

Использование математической подсказки:

- не уменьшает XP;
- не создаёт штраф;
- записывается как часть математической попытки;
- может влиять на Mastery evidence по правилам Mastery System.

# 11. Defeat

При поражении:

- подтверждённый XP сохраняется;
- достигнутый RPG Level сохраняется;
- подтверждённые Coins сохраняются;
- подтверждённые предметы сохраняются;
- текущие неподтверждённые награды попытки могут быть потеряны;
- подтверждённый Mastery сохраняется.

Поражение не является откатом общего прогресса.

# 12. RPG Level

RPG Level: 1..30.

Утверждённая XP Policy использует:

~~~text
XPRequired(L) = 50 × (L − 1)²
~~~

Уровень не является математическим уровнем.

# 13. RPG Level не открывает математику автоматически

Рост RPG Level не означает автоматическое открытие математического Skill.

Математические prerequisites и Mastery остаются обязательными.

# 14. RPG Unlocks

RPG Level может открывать:

- новые зоны;
- сюжет;
- игровые механики;
- предметы;
- способности;
- развитие дома;
- стадии питомца;
- визуальные статусы.

Такие unlocks относятся к Game Progression.

# 15. Milestones

Особые уровни:

~~~text
5
10
15
20
25
30
~~~

дают major progression milestones согласно RPG Progression.

Level 30 завершает первую большую сюжетную арку MVP.

# 16. GameProgressionEvent

Минимальная структура:

~~~text
GameProgressionEvent
    eventId
    playerId
    eventType
    sourceId
    sessionId
    outcome
    timestamp
    metadata
~~~

eventId уникален и используется для idempotency.

# 17. Event Types

Минимально:

~~~text
QUEST_COMPLETED
COMBAT_VICTORY
DISCOVERY
PUZZLE_COMPLETED
STORY_MILESTONE
HOME_MILESTONE
ACHIEVEMENT
LEVEL_COMPLETED
~~~

Набор может расширяться без изменения основного контракта.

# 18. RewardBundle

Один подтверждённый Game Event может создать:

~~~text
RewardBundle
    xpDelta
    coinsDelta
    loot
    reason
~~~

Все эффекты одного события относятся к одному progression commit.

# 19. RPG Level Update

До commit:

~~~text
levelBefore
totalXpBefore
~~~

После расчёта:

~~~text
levelAfter
totalXpAfter
newUnlocks
~~~

Level-up является производным результатом изменения XP.

# 20. ProgressionCommit

~~~text
ProgressionCommit
    eventId
    rewardBundle
    levelBefore
    levelAfter
    unlocks
    committedAt
~~~

Commit должен быть атомарным.

# 21. Атомарность

Нельзя допустить:

~~~text
XP записан
Coins потеряны
~~~

или:

~~~text
Coins выданы
XP не записан
~~~

или:

~~~text
Level-up показан
unlock не сохранён
~~~

Игровое событие и его progression effects должны сохраняться согласованно.

# 22. Idempotency

Повторная обработка одного eventId не должна повторно выдавать награду.

~~~text
eventId = ABC
→ commit
→ network retry
→ eventId = ABC
→ no second reward
~~~

Это обязательно для offline-first и будущей синхронизации.

# 23. Offline-first

В MVP progression работает локально:

~~~text
Game Event
   ↓
Local Progression Store
~~~

Интернет не требуется для получения XP, расчёта RPG Level, получения Coins и прохождения игры.

# 24. Будущий сервер

~~~text
Local Event
   ↓
Sync
   ↓
Server Validation
   ↓
Authoritative Progression
~~~

Сервер в будущем должен проверять eventId, последовательность событий, допустимость награды, повторную обработку, критические unlocks и XP/Coins/Loot.

# 25. Loot

Loot является частью Reward/Economy, а не Math Engine.

Для offline-first результат должен быть воспроизводимым или валидируемым. Для критических наград рекомендуется deterministic seed + Reward Policy.

# 26. Ownership

## Math Engine / Mastery

Отвечает за:

- математическую корректность;
- Mastery;
- Skill availability;
- математические prerequisites.

## Adaptive Engine

Отвечает за:

- следующий Skill;
- Difficulty;
- Mode;
- Context.

## Game Progression

Отвечает за:

- Game Events;
- XP;
- RPG Level;
- game unlocks.

## Reward/Economy

Отвечает за:

- Coins;
- Loot;
- reward policy.

## Game Engine

Отвечает за:

- combat;
- quests;
- exploration;
- world;
- home;
- puzzle;
- story.

# 27. Запрет прямого доступа

Неверно:

~~~text
Combat → addXp(100)
~~~

Правильно:

~~~text
Combat
 ↓
validated COMBAT_VICTORY
 ↓
Game Progression
 ↓
Reward Policy
 ↓
XP
~~~

Игровые системы создают события, а не напрямую изменяют глобальный progression state.

# 28. Math Answer Boundary

Неверно:

~~~text
correct answer
→ XP +10
~~~

Правильно:

~~~text
correct answer
→ successful game action
→ validated game event
→ reward policy
→ XP
~~~

# 29. Mastery Boundary

Неверно:

~~~text
RPG Level 10
→ Mastery +1
~~~

или:

~~~text
XP 1000
→ Skill unlocked
~~~

Правильно:

~~~text
Math Attempt
→ Math Engine
→ Mastery System
→ Adaptive
~~~

# 30. Repeat / Best Result

Для повторяемых игровых активностей существует BestResult:

~~~text
levelId
bestOutcome
bestScore
bestCompletionState
~~~

Reward Policy сравнивает новый результат с сохранённым.

Это поддерживает улучшение результата, ограниченный bonus XP и повторную игру без бесконечного XP farming.

# 31. First Completion

First Completion должна быть явно отслеживаема:

~~~text
firstCompletedAt
firstRewardGranted
~~~

Она не должна определяться только по наличию текущего уровня.

# 32. Reward Event

Для аудита:

~~~text
RewardEvent
    rewardEventId
    sourceEventId
    rewardType
    amount
    timestamp
    policyVersion
~~~

Один sourceEventId может породить несколько типов наград, но они принадлежат одному progression commit.

# 33. Policy Version

Reward Policy должна быть версионирована:

~~~text
rewardPolicyVersion
xpPolicyVersion
repeatPolicyVersion
~~~

Это позволяет воспроизводить старые решения.

# 34. Level-up Event

~~~text
XP change
   ↓
level calculation
   ↓
LEVEL_UP
~~~

UI получает событие и показывает анимацию, новый уровень, unlocks и milestone, если применимо.

# 35. Multiple Level-up

Одно событие может дать достаточно XP для нескольких уровней. Система должна корректно определить все промежуточные unlocks.

# 36. No Negative XP

В MVP нет отрицательного XP.

Не допускается:

~~~text
defeat → XP -100
~~~

или:

~~~text
wrong answer → XP -10
~~~

# 37. No Mastery Rollback

Game Progression никогда не откатывает Mastery.

Даже при defeat, failed quest, lost battle или потере текущей награды.

Mastery определяется Math Engine/Mastery System по математическим событиям.

# 38. Persistence Order

~~~text
1. Validate Game Event
2. Evaluate eligibility
3. Calculate rewards
4. Calculate RPG Level
5. Calculate unlocks
6. Commit atomically
7. Publish UI/event notifications
~~~

UI notification не является источником истины.

# 39. Failure Handling

Если commit не завершён:

- progression state не должен оказаться частично изменённым;
- событие остаётся retryable;
- повторная обработка использует eventId;
- после восстановления операция безопасно завершается.

# 40. Telemetry

Минимально:

~~~text
eventId
playerId
eventType
sourceId
rewardPolicyVersion
xpPolicyVersion
xpDelta
coinsDelta
lootIds
levelBefore
levelAfter
timestamp
~~~

Для отладки полезны repeatState, bestResultBefore, bestResultAfter и eligibilityReason.

# 41. QA

Минимальный набор:

~~~text
Game Event Validation
Reward Eligibility
First Completion
Repeat Completion
Best Result Improvement
Anti-Farming
XP Calculation
RPG Level Calculation
Unlock Calculation
Idempotency
Atomic Commit
Defeat Persistence
Offline Retry
Multiple Level-Up
Policy Versioning
~~~

# 42. Пример успешного боя

~~~text
Player starts combat
        ↓
Adaptive selects math task
        ↓
Player answers
        ↓
Math Engine validates
        ↓
Combat resolves
        ↓
Enemy defeated
        ↓
COMBAT_VICTORY
        ↓
Reward Policy
        ↓
XP + Coins + Loot
        ↓
RPG Level calculation
        ↓
Atomic commit
        ↓
UI
~~~

# 43. Пример поражения

~~~text
Player starts combat
        ↓
Math error
        ↓
combat failure
        ↓
player defeated
        ↓
no victory reward
        ↓
confirmed XP/Coins/Mastery remain
        ↓
return to level entry
~~~

# 44. Пример повторного уровня

~~~text
First completion
    ↓
full eligible XP
    ↓
saved best result

Repeat
    ↓
same/below best
    ↓
XP = 0

Repeat
    ↓
improved best result
    ↓
limited improvement XP
~~~

Coins/Loot follow their own repeat policy.

# 45. Главный архитектурный принцип

> **Game Engine сообщает, что произошло в игре. Game Progression определяет, какая награда положена. Reward/Economy рассчитывает игровые ресурсы. RPG Progression определяет уровень и игровые unlocks. Math Engine остаётся владельцем математического прогресса.**

# 46. MVP API

Концептуально:

~~~text
recordGameEvent(event)
evaluateReward(event, state)
calculateRpgLevel(totalXp)
calculateUnlocks(level)
commitProgression(commit)
getProgress(playerId)
~~~

Фактический API будет определён технической архитектурой.

# 47. Главный контракт

~~~text
GameEvent
    ↓
GameProgressionPolicy
    ↓
ProgressionCommit
    ↓
GameProgressionStore
~~~

Этот контракт независим от UI и конкретного игрового режима.

# 48. Статус документа

**13_GAME_ENGINE_PROGRESSION.md v1.0 — APPROVED**

Следующие документы должны использовать эту границу:

- Combat;
- Quests;
- Economy;
- Items/Equipment;
- Home/Territory;
- Pet;
- World/Story;
- Server Sync.

Главное правило:

> **Математика определяет математический прогресс. Игровые события определяют игровой прогресс. XP, Coins, Loot и RPG Level не заменяют Mastery.**
