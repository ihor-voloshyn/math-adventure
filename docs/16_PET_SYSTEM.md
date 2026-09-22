# 16_PET_SYSTEM.md

**Проект:** Math Adventure  
**Документ:** Pet System — система питомца  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

# 1. Назначение
Pet System определяет правила питомца-компаньона Math Adventure. Питомец усиливает эмоциональную связь с игрой, сопровождает героя, визуально развивается вместе с RPG Progression и даёт небольшие game-owned бонусы.

Главный принцип: питомец помогает ребёнку чувствовать прогресс героя, но не компенсирует отсутствие математического прогресса.

# 2. MVP
В MVP ребёнок получает одного постоянного питомца. Доступны KITTEN и PUPPY. Выбор влияет прежде всего на внешний вид и небольшой тематический профиль бонусов.

Питомец не является отдельным классом героя и не определяет Knight/Mage, Curriculum, Skill Graph, Mastery или Adaptive Policy.

# 3. Математическая граница
Питомец никогда не решает математическую задачу, не выдаёт правильный ответ вместо ребёнка, не изменяет Mastery, не открывает Skill, не обходиt prerequisite и не управляет Math Difficulty.

Недопустимо: Pet bonus → Mastery gate bypass.

# 4. Рост
Рост питомца связан с RPG Level, а не напрямую с Mastery.

Стадии MVP:
- RPG 1: Baby
- RPG 5: Young
- RPG 10: Explorer Companion
- RPG 20: Loyal Companion
- RPG 30: Guardian Companion

Между milestone допустимы небольшие визуальные изменения. Отдельный Pet XP в MVP не требуется.

# 5. Визуальная прогрессия
Каждая основная стадия должна быть заметна ребёнку. Могут меняться размер, пропорции, шерсть, аксессуары, анимации, эффекты и поведение.

Рост визуально связан с Home/Territory и утверждёнными RPG milestones 5, 10, 15, 20, 25, 30.

# 6. Home
У питомца есть место в Home: от маленькой лежанки/домика до выделенной зоны в развитом доме или замке.

Home не создаёт отдельного математического прогресса питомца.

# 7. Присутствие в мире
Питомец может сопровождать героя в деревне, лесу, исследовании, квестах и разрешённых сценах подземелья/боя. Game/World Engine определяет допустимость физического присутствия.

# 8. Бонусы
Бонусы питомца небольшие и относятся только к игровым системам.

Допустимые направления MVP:
- Exploration: небольшая помощь в обнаружении обычных находок.
- Combat: небольшой защитный/восстановительный модификатор.
- Economy/Find: небольшая вероятность дополнительной обычной находки.

Конкретные числовые значения задаются Game/Economy configuration, а не этим документом.

Питомец не может мгновенно побеждать врагов, давать постоянную неуязвимость, обходить RPG unlock или делать математические ограничения несущественными.

# 9. Видовой профиль
Kitten может иметь тематическую ориентацию на exploration/discovery, Puppy — на defense/recovery. Разница должна быть небольшой: ни один вид не должен становиться обязательным для прогрессии.

# 10. Affection / Happiness
Полноценная система ухода не входит в MVP. Не требуются кормление по таймеру, болезнь, смерть, наказание за отсутствие ухода или ежедневные обязательства.

Питомец должен быть источником позитивной связи с игрой, а не дополнительной обязанностью.

# 11. Реакции
Питомец может радоваться победе, реагировать на находку, встречать героя в Home, реагировать на новый предмет и повышение RPG Level. Реакции не влияют на математический результат.

# 12. Бой
Питомец может визуально присутствовать в бою и предоставлять небольшой game-owned modifier.

Цепочка: Player action → Adaptive → Math task → Math Engine → Combat resolution → Pet modifier → Game result.

Combat System определяет, когда и как применяется modifier. Pet System не проверяет математический ответ.

# 13. Неправильный ответ и подсказки
Неправильный ответ не позволяет питомцу исправить результат, изменить Mastery или отменить математическое событие.

Math Hint остаётся бесплатной. Питомец может участвовать в поддерживающей анимации, но не выдаёт ответ.

# 14. PET_ACCESSORY
Items & Equipment уже определяет слот PET_ACCESSORY. Аксессуар может менять внешний вид и небольшой game modifier, но не Skill, Mastery, Math Difficulty или prerequisite.

После Equip, Unequip, Restart, Load, Offline recovery и Sync визуальное состояние должно соответствовать сохранённому.

# 15. Получение
Рекомендуемый ранний flow: встреча с kitten/puppy → выбор компаньона → питомец присоединяется → место питомца открывается в Home.

Питомец не требует покупки за реальные деньги.

# 16. Модель состояния
PetDefinition содержит статические данные: petId, species, growthStages, allowedAccessoryCategories, bonusProfile.

PetInstance содержит: playerId, petId, species, growthStage, active, equippedAccessoryIds, acquisitionEventId.

Для MVP активен один питомец.

# 17. Persistence
Получение питомца, выбор вида, рост стадии и Equip/Unequip аксессуара должны сохраняться атомарно относительно критического изменения.

Подтверждённый питомец не удаляется при поражении.

# 18. Offline-first и Sync
Питомец должен полноценно работать offline: сопровождение, реакции, рост по локальному RPG Level, допустимые бонусы, Equip/Unequip и persistence.

Будущая синхронизация: Local Pet State → Sync Event → Server Validation → Authoritative Pet State. События должны быть идемпотентными.

# 19. Защита от подделки
В будущем сервер проверяет допустимость стадии, аксессуара, источника получения и последовательности RPG progression. Клиент не считается полностью доверенным.

# 20. RPG Level и откат
RPG Level не уменьшается из-за ошибки, поражения, пропуска или подсказки. Поэтому подтверждённая стадия питомца не должна неожиданно откатываться из-за обычного defeat.

# 21. Mastery independence
Mastery не является прямым условием роста питомца. RPG Level → Pet growth. Mastery → mathematical progression.

# 22. Rewards
Pet System не рассчитывает XP, Coins или Loot. Game Progression остаётся владельцем reward semantics.

Питомец не должен создавать farming-loop вида Pet bonus → repeat easy task → infinite rewards.

# 23. Adaptive independence
Pet modifier не может сделать Skill доступным, повысить/понизить Mastery, изменить prerequisite или заставить Adaptive выбирать более лёгкую задачу ради награды.

# 24. Deterministic behavior
Если modifier использует случайность, результат должен быть воспроизводим в QA/offline replay по state + event + policyVersion + seed.

# 25. Safe fallback
Если modifier недоступен или некорректен, используется безопасный default: no pet bonus. Ошибка Pet System не должна блокировать игровой цикл.

# 26. Telemetry
Рекомендуемые поля: petEventId, playerId, petId, species, growthStage, eventType, modifierId, policyVersion, timestamp. Для QA также rpgLevel, petStageBefore, petStageAfter, randomSeed.

# 27. Запрещённые решения
Не допускаются: изменение Mastery питомцем; решение математики питомцем; bypass prerequisite; выбор следующего math task питомцем; отдельный Pet XP в MVP; обязательный уход; потеря питомца при defeat.

# 28. Минимальный интерфейс
getPet(playerId)
getActivePet(playerId)
getGrowthStage(playerId)
syncGrowthWithRpgLevel(playerId, rpgLevel)
getAvailableModifiers(playerId, context)
getAllowedAccessories(petId)
equipAccessory(playerId, itemId)
unequipAccessory(playerId, itemId)

# 29. QA
Минимум: Pet Acquisition, Species Selection, Persistence, Growth at 5/10/20/30, No Growth Rollback on Defeat, No Mastery Bypass, No Prerequisite Bypass, Accessory Equip/Unequip, Visual Consistency, Combat/Exploration Modifier, Safe Fallback, Deterministic Modifier, Offline Persistence, Sync Idempotency.

# 30. Acceptance Criteria
1. Ребёнок может получить kitten или puppy.
2. Активен один питомец.
3. Питомец сохраняется между сессиями.
4. Питомец визуально растёт вместе с RPG Level.
5. Рост не зависит напрямую от Mastery.
6. Питомец присутствует в допустимых игровых контекстах.
7. Бонусы малы и game-owned.
8. Питомец не решает математические задачи.
9. Питомец не обходит Mastery/prerequisite.
10. PET_ACCESSORY работает через Items & Equipment.
11. Defeat не удаляет подтверждённого питомца.
12. Состояние работает offline-first.
13. Sync поддерживает идемпотентность.
14. Случайные modifiers воспроизводимы в deterministic mode.
15. Ошибка modifier не блокирует игру.

# 31. Архитектурная граница
RPG Progression → Pet System (Growth, State, Visual, Small Modifiers).

Items & Equipment → PET_ACCESSORY → Pet System.

Pet System → World/Home и Combat через состояние/разрешённые modifiers.

Math/Mastery/Adaptive не передают питомцу математическую власть.

# 32. Главный принцип
> **Питомец — эмоциональный и игровой компаньон, а не образовательный чит.**

> **RPG Level развивает питомца; Mastery развивает математическую прогрессию.**

> **Pet System предоставляет состояние, визуальный рост и небольшие game modifiers, но не владеет математической истиной, Mastery, Adaptive Policy, XP или Coins.**

# 33. Статус
**`16_PET_SYSTEM.md v1.0` — APPROVED**

Документ является архитектурной основой для `17_HOME_TERRITORY.md`, `18_QUEST_SYSTEM.md`, `19_WORLD_STORY.md` и будущей технической реализации Pet System.