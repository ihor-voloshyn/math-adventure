# 14_COMBAT_SYSTEM.md

**Проект:** Math Adventure  
**Документ:** Combat System — тактический пошаговый бой  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

---

# 1. Назначение

Combat System определяет математически управляемый тактический бой Math Adventure.

Главный принцип:

> **Математика определяет успешность действия игрока; Combat System превращает математический результат в игровое последствие.**

Combat не вычисляет математическую правильность самостоятельно.

# 2. Основной цикл

~~~text
Start Combat
    ↓
Player Turn
    ↓
Choose Action
    ↓
Adaptive Engine selects math task
    ↓
Player Answer
    ↓
Math Engine validates
    ↓
Combat resolves action
    ↓
Enemy / Player state changes
    ↓
Next Turn
    ↓
Victory / Defeat / Continue
~~~

# 3. Пошаговая модель

Бой является turn-based.

В MVP:

- игрок действует первым;
- затем действует противник;
- после этого начинается следующий раунд;
- бой продолжается до Victory, Defeat или допустимого Escape.

Нет real-time реакции, требующей скорости математического ответа.

# 4. Hearts / HP

Игрок и враги имеют здоровье.

Игрок использует Hearts как понятное детское представление HP.

Пример:

~~~text
❤️ ❤️ ❤️ ❤️
~~~

Потеря всех Hearts означает поражение текущего боя.

Количество Hearts и числовые HP являются отдельной балансировочной настройкой.

# 5. Player Action

Основные действия MVP:

- Attack;
- Defend;
- Ability, если способность уже открыта;
- Flee, если конкретный бой разрешает побег.

Каждое действие, которое требует математического решения, проходит через Math Engine.

# 6. Attack

~~~text
Attack
  ↓
Math Task
  ↓
Correct
  ↓
Damage
~~~

При правильном ответе атака успешна.

Размер урона определяется Combat/Economy правилами, а не Math Engine.

# 7. Incorrect Answer

Неправильный ответ не означает автоматическую смерть.

В MVP неправильный ответ может привести к:

- промаху;
- потере текущего действия;
- снижению эффективности;
- возможности ответного действия противника.

Точное последствие зависит от типа действия и Combat Policy.

# 8. Math Hint

Math Hint:

- бесплатна;
- не уменьшает XP;
- не уменьшает подтверждённую Mastery;
- может быть использована в бою;
- фиксируется как часть математической попытки.

Использование подсказки не должно превращать бой в наказание за обучение.

# 9. Damage

Damage является игровой величиной.

Math Engine возвращает:

~~~text
CORRECT / INCORRECT / SKIPPED
~~~

Combat System использует этот результат вместе с Combat Policy.

Math Engine не знает:

- Armor;
- HP;
- Crit;
- Damage;
- Enemy Stats.

# 10. Defense

Defend может использоваться как математическое действие или как доступное игровое действие, в зависимости от конкретной боевой конфигурации.

Если действие требует математики, оно получает задачу через Adaptive Engine.

При успешной защите игрок получает уменьшение следующего входящего урона.

# 11. Abilities

Способности являются игровыми объектами.

Они могут иметь:

- cooldown;
- resource cost;
- damage modifier;
- defense modifier;
- special effect.

Но способность не может обходить математический Skill Gate, если для её применения требуется математическое знание.

# 12. Critical Actions

Некоторые способности или атаки могут иметь усиленный результат при:

- правильном ответе;
- подходящей сложности;
- специальном игровом условии.

Но:

> **Игрок не должен получать критический результат только за быстрый ответ.**

Скорость не является обязательным условием математической компетентности.

# 13. Enemy

Enemy имеет:

~~~text
enemyId
name
maxHp
currentHp
attack
defense
abilities
lootProfile
combatProfile
~~~

Enemy Stats принадлежат Game Engine.

# 14. Enemy Types

MVP может использовать несколько базовых ролей:

- Basic;
- Defensive;
- Fast;
- Heavy;
- Magical;
- Boss.

Роли отличаются поведением и параметрами, а не математическим curriculum.

# 15. Enemy Math Profile

Противник не получает собственный независимый набор математических правил.

Combat передаёт контекст:

~~~text
enemyType
combatMode
availableActions
~~~

Adaptive Engine выбирает подходящий Skill и Difficulty.

# 16. Player First

Игрок действует первым в MVP.

Это снижает ощущение внезапного наказания и даёт ребёнку понятный контроль над боем.

# 17. Enemy Turn

После завершения действия игрока:

~~~text
if enemy alive
    ↓
Enemy Turn
~~~

Противник выбирает игровое действие по Combat AI.

Enemy AI не решает математическую задачу вместо ребёнка.

# 18. Flee

Flee доступен только там, где это разрешено Encounter Policy.

Побег:

- завершает текущий бой;
- не считается Victory;
- не выдаёт Victory XP;
- не уничтожает подтверждённый прогресс;
- возвращает игрока в допустимое игровое состояние.

# 19. Victory

Victory наступает, когда все обязательные враги побеждены.

~~~text
Enemy HP = 0
        ↓
Victory
        ↓
COMBAT_VICTORY
        ↓
Game Progression
        ↓
XP / Coins / Loot
~~~

Reward выдаётся через Game Progression, а не напрямую Combat.

# 20. Defeat

Defeat наступает, когда:

~~~text
Player Hearts = 0
~~~

После Defeat:

- бой заканчивается;
- Victory reward за незавершённый бой не выдаётся;
- подтверждённый XP сохраняется;
- подтверждённый Mastery сохраняется;
- подтверждённые Coins/Loot сохраняются;
- текущие неподтверждённые награды могут быть потеряны;
- игрок возвращается к входу в уровень согласно Game Design.

# 21. Повторный бой

Повтор боя не должен создавать бесконечный XP farming.

Combat Victory создаёт Game Event, а Game Progression применяет Repeat Policy.

Первое значимое прохождение может дать полный XP.

Повтор без улучшения сохранённого результата не даёт XP.

# 22. Combat State

Минимальная модель:

~~~text
CombatState
    combatId
    playerId
    encounterId
    turnNumber
    phase
    playerState
    enemyStates
    pendingAction
    result
~~~

# 23. Combat Phase

Минимально:

~~~text
PLAYER_TURN
ENEMY_TURN
VICTORY
DEFEAT
FLED
~~~

# 24. Pending Action

До математического ответа действие является pending.

~~~text
Player chooses Attack
        ↓
Pending Action = ATTACK
        ↓
Math Task
        ↓
Answer
        ↓
Resolve Action
~~~

Это позволяет не менять HP до подтверждения математического результата.

# 25. Atomic Combat Resolution

Математический результат и его игровое последствие должны обрабатываться согласованно.

Нельзя получить:

~~~text
Damage applied
but attempt not recorded
~~~

или:

~~~text
Math attempt recorded
but combat state lost
~~~

Критические persistence boundaries определяются общей Offline-first архитектурой.

# 26. Math Attempt

Каждое математическое действие создаёт Math Attempt через Math Engine.

Combat использует только валидированный результат.

~~~text
Math Engine
    ↓
AttemptEvaluation
    ↓
Combat Resolution
~~~

# 27. SKIPPED

SKIPPED не считается INCORRECT.

Combat Policy может определить:

- действие пропущено;
- ход потерян;
- действие отменено;
- альтернативное действие предложено.

Но пропуск не должен автоматически снижать Mastery.

# 28. Repeated Errors

Серия ошибок может приводить к:

- меньшей Difficulty в следующих задачах;
- смене представления;
- подсказке;
- Review;
- Remediation.

Эти решения принимает Adaptive Engine.

Combat не меняет Mastery самостоятельно.

# 29. High Mastery

Высокая Mastery не должна превращать бой в автоматическую победу.

Она позволяет Adaptive Engine выбирать подходящий уровень математического вызова.

Игровые характеристики героя и экипировка остаются отдельными системами.

# 30. RPG Level

RPG Level может открывать:

- новые способности;
- экипировку;
- новые зоны;
- новые типы противников.

Но RPG Level не заменяет математические prerequisites.

# 31. Equipment

Оружие и экипировка могут менять:

- damage;
- defense;
- modifiers;
- доступные способности.

Но экипировка не должна давать возможность обойти математический ответ там, где он является обязательной частью действия.

# 32. Combat Difficulty

Combat Difficulty не является математической Difficulty.

Разделяем:

~~~text
Combat Difficulty
≠
Math Difficulty
~~~

Math Difficulty выбирается Adaptive Engine.

Combat Difficulty задаётся Encounter/Combat configuration.

# 33. Boss

Boss имеет:

- увеличенное здоровье;
- несколько фаз;
- уникальные способности;
- специальную encounter logic;
- более значимые rewards.

Boss не должен требовать математического уровня выше текущей допустимой зоны только потому, что это Boss.

# 34. Boss Phases

Фаза Boss может менять:

- набор действий;
- защиту;
- доступные способности;
- визуальное состояние;
- допустимые математические контексты.

Adaptive Engine всё равно соблюдает математические ограничения ребёнка.

# 35. No Math Instant Kill

Нельзя делать:

~~~text
one correct answer
→ boss instantly dies
~~~

если это не специально заданная сюжетная механика.

Математический успех должен быть значимым, но бой должен сохранять игровую структуру.

# 36. Combat Feedback

После математического ответа ребёнок должен сразу понимать:

- правильно ли решена задача;
- получилось ли действие;
- сколько урона нанесено;
- что сделал противник;
- сколько Hearts осталось.

Feedback должен быть быстрым и визуально понятным.

# 37. Child-friendly presentation

Не показывать технические параметры:

~~~text
damageCoefficient = 1.37
adaptiveScore = 0.82
~~~

Вместо этого:

~~~text
Great!
⚔️ Attack!
Enemy -2 ❤️
~~~

# 38. Failure Feedback

Неправильный ответ не должен звучать как:

~~~text
You are bad at math
~~~

Предпочтительно:

~~~text
Not quite!
Try again.
~~~

или игровое нейтральное сообщение.

# 39. Combat Camera

MVP использует третье лицо с вращаемой камерой.

Во время математического задания камера может:

- слегка приблизиться;
- сфокусироваться на персонаже/противнике;
- затем вернуться к боевой позиции.

Математический UI должен оставаться читаемым.

# 40. Combat Start

При начале боя показываются:

- противники;
- состояние героя;
- доступные действия;
- короткая информация об encounter.

Не следует сразу перегружать ребёнка сложной статистикой.

# 41. Combat End

Victory:

~~~text
Victory!
XP / Coins / Loot
Continue
~~~

Defeat:

~~~text
Defeated
Retry
Return
~~~

Конкретные награды рассчитываются Game Progression.

# 42. Combat Telemetry

Минимально:

~~~text
combatId
encounterId
turnNumber
actionType
mathAttemptId
answerResult
damage
damageTaken
enemyId
enemyHpBefore
enemyHpAfter
playerHeartsBefore
playerHeartsAfter
result
timestamp
~~~

# 43. QA

Минимальный набор:

~~~text
Player First Turn
Attack Correct
Attack Incorrect
Attack Skipped
Enemy Turn
Defend
Flee
Victory
Defeat
Boss Phase
Repeated Combat
XP Anti-Farming
Atomic Resolution
Math Attempt Link
Mastery Independence
RPG Level Independence
Equipment Effects
Deterministic Replay
Offline Recovery
~~~

# 44. Deterministic Replay

Combat state changes должны быть воспроизводимыми при одинаковом:

~~~text
combatState
action
validatedMathResult
combatPolicyVersion
randomSeed
~~~

Случайность допускается для enemy choice/loot variation, но должна быть контролируемой для QA.

# 45. Архитектурная граница

~~~text
ADAPTIVE ENGINE
    ↓
Math Task
    ↓
MATH ENGINE
    ↓
Validated Attempt
    ↓
COMBAT SYSTEM
    ↓
Game Event
    ↓
GAME PROGRESSION
    ↓
XP / Coins / Loot / RPG Level
~~~

Combat не является владельцем:

- математической корректности;
- Mastery;
- Adaptive Policy;
- XP policy;
- Coins policy.

# 46. MVP API

Концептуально:

~~~text
startCombat(encounter)
getState(combatId)
chooseAction(combatId, action)
submitMathResult(combatId, attemptEvaluation)
resolveTurn(combatId)
flee(combatId)
getResult(combatId)
~~~

Фактический API будет определён технической архитектурой.

# 47. Главный принцип

> **Игрок выбирает действие. Adaptive Engine выбирает математический вызов. Math Engine проверяет математику. Combat System разрешает действие. Game Progression выдаёт игровую награду.**

# 48. Статус документа

**14_COMBAT_SYSTEM.md v1.0 — APPROVED**

Следующие документы:

- Items/Equipment;
- Pet;
- Home/Territory;
- Quests;
- World/Story;
- Server Sync.

Главное правило:

> **Combat превращает математический результат в игровое последствие, но не становится владельцем математической логики или игрового прогресса.**
