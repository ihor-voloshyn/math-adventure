# 15_ITEMS_EQUIPMENT.md

**Проект:** Math Adventure  
**Документ:** Items & Equipment — предметы, экипировка и игровой инвентарь  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

---

# 1. Назначение

Items & Equipment определяет предметы, экипировку, их игровые характеристики и связь с RPG-прогрессией.

Главный принцип:

> **Предметы усиливают героя внутри RPG-системы, но не заменяют математическое развитие ребёнка.**

# 2. Граница ответственности

Items/Equipment отвечает за:
- определения предметов;
- категории;
- rarity;
- характеристики;
- экипировку;
- визуальное отображение;
- inventory;
- получение и расходование предметов;
- ограничения экипировки;
- связь с RPG Level.

Не отвечает за:
- математическую правильность;
- Mastery;
- Adaptive Policy;
- выбор математической задачи;
- XP policy;
- выдачу XP напрямую.

# 3. MVP Inventory

MVP поддерживает инвентарь без ограничения веса.

```text
ItemInstance
    instanceId
    itemId
    quantity
    acquiredAt
    source
```

Ограничение количества предметов не является частью MVP.

# 4. Item Definition

```text
ItemDefinition
    itemId
    name
    category
    rarity
    description
    visualId
    stats
    requirements
```

Definition отделён от конкретного экземпляра.

# 5. Категории

```text
WEAPON
ARMOR
HELMET
ACCESSORY
CONSUMABLE
QUEST_ITEM
DECORATION
```

# 6. Equipment Slots

```text
WEAPON
ARMOR
HELMET
ACCESSORY
PET_ACCESSORY
```

# 7. Rarity

```text
COMMON
UNCOMMON
RARE
EPIC
LEGENDARY
```

Rarity является игровым свойством, а не оценкой математических знаний.

# 8. Stats

Предмет может изменять:
- Attack;
- Defense;
- Hearts/HP;
- ability modifiers;
- специальные combat modifiers.

В MVP число характеристик должно оставаться небольшим.

# 9. Weapon / Armor

Оружие может изменять attackPower и abilityModifier.

Armor может изменять Defense, максимальные Hearts и специальные защитные эффекты.

Предметы не определяют математический Skill.

# 10. Consumable

Consumable может восстанавливать Hearts или давать временный игровой эффект.

Consumable не может восстанавливать Mastery или автоматически решать математическую задачу.

# 11. Quest Item / Decoration

Quest Item используется квестами и сюжетом.

Decoration используется для дома и территории и не обязана давать combat bonus.

# 12. Equipment State

```text
EquipmentState
    weaponInstanceId
    armorInstanceId
    helmetInstanceId
    accessoryInstanceId
    petAccessoryInstanceId
```

Пустой слот допустим.

# 13. Equip / Unequip

При экипировке проверяются:
1. предмет существует;
2. предмет принадлежит игроку;
3. слот совместим;
4. требования выполнены;
5. состояние предмета допустимо.

Операция атомарна.

При снятии предмет возвращается в Inventory.

# 14. RPG Level Requirement

Некоторые предметы могут иметь `requiredRpgLevel`.

Это RPG gate, а не математический gate.

RPG Level может открыть доступ к новому предмету, но не открывает математические Skills.

# 15. Mastery Boundary

В MVP предметы не используют Mastery как прямое условие экипировки.

Запрещено делать экипировку зависимой от математического Mastery без отдельного продуктового решения.

# 16. Visual Equipment

Критическое правило:

> **Экипированное визуальное оборудование не может быть скрыто при сохранении его gameplay stats.**

После Equip, Restart, Load и Sync визуальное состояние должно соответствовать Equipment State.

# 17. Item Acquisition

Предмет может быть получен через:
- Quest;
- Combat Victory;
- Discovery;
- Story milestone;
- Chest/reward;
- Home/Territory milestone;
- Achievement;
- Shop.

Источник — Game Event.

# 18. Item Reward Boundary

Combat и Quest не должны напрямую изменять Inventory.

```text
Game Event
   ↓
Reward Policy
   ↓
Loot / Item
   ↓
Inventory Commit
```

Если событие выдаёт XP + Coins + Item, эффекты должны быть согласованы одним progression commit.

# 19. Loot

Loot Table определяет допустимые предметы источника.

```text
LootTable
    sourceId
    eligibleItems
    weights
    guarantees
```

Для offline-first случайность должна быть контролируемой, воспроизводимой или проверяемой; seed должен быть связан с игровым событием.

# 20. Duplicate Items

Дубликаты разрешены.

Stackable items увеличивают quantity.

Equipment остаётся отдельными ItemInstance.

# 21. Replacement

При замене экипировки старый предмет возвращается в Inventory.

Новый предмет не уничтожает старый автоматически.

# 22. No Permanent Destruction in MVP

Экипировка не ломается и не уничтожается из-за поражения.

Defeat не удаляет Inventory, экипировку, подтверждённый Loot или экипировку питомца.

Текущие неподтверждённые reward drops могут не сохраниться.

# 23. Upgrade / Sets

Сложный crafting и upgrade не входят в MVP.

Обязательная set-bonus система также не входит в MVP.

Она может быть добавлена позднее.

# 24. Legendary

Legendary предметы могут иметь уникальный внешний вид, специальный эффект, сюжетную связь или редкую анимацию.

Они не делают математический контент необязательным.

# 25. Shop / Economy

Coins принадлежат Economy.

Item System отвечает за предмет.

```text
Shop
 ↓
Economy
 ↓
Coins
 ↓
Item Inventory
```

Покупка атомарна: Coins списаны и Item добавлен либо операция полностью отклонена.

# 26. Math and Shop

Математическая задача может быть условием игрового действия в магазине:

```text
Purchase
 ↓
Math Task
 ↓
Correct
 ↓
Purchase allowed
```

Цена и списание Coins принадлежат Economy.

# 27. Persistence / Anti-cheat

Inventory и Equipment State сохраняются локально.

Критические Item events должны содержать:

```text
eventId
sourceEventId
itemId
quantity
timestamp
policyVersion
```

Повторная обработка source event не должна создавать дубликат награды.

Будущий сервер валидирует ownership, acquisition, purchases и critical rewards.

# 28. Presentation

Ребёнку показывается простой результат:

```text
Sword of Sparks
⚔️ 7
```

Технические audit fields остаются внутренними.

# 29. QA

Минимально:
- Add Item;
- Duplicate Item;
- Stack Item;
- Equip / Unequip;
- Replacement;
- Requirement Check;
- Inventory Persistence;
- Equipment Persistence;
- Visual State Consistency;
- Defeat Preservation;
- Atomic Purchase;
- Atomic Reward;
- Duplicate Event Protection;
- Deterministic Loot;
- Offline Recovery.

# 30. Архитектурная граница

```text
GAME EVENT
    ↓
REWARD / ECONOMY
    ↓
ITEM / INVENTORY
    ↓
EQUIPMENT
    ↓
COMBAT STATS / VISUAL
```

Math Engine остаётся отдельным владельцем математического результата.

# 31. MVP API

```text
getInventory(playerId)
getEquipment(playerId)
addItem(reward)
equip(playerId, itemInstanceId)
unequip(playerId, slot)
canEquip(playerId, itemInstanceId)
getItemDefinition(itemId)
```

# 32. Основной принцип

> **Equipment делает героя сильнее внутри игры, но никогда не заменяет математическое обучение.**

> **Game Event является источником получения предмета. Inventory хранит владение. Equipment определяет активные предметы. Combat использует их характеристики.**

# 33. Статус документа

**15_ITEMS_EQUIPMENT.md v1.0 — APPROVED**

Следующие документы:
- Pet;
- Home/Territory;
- Quests;
- World/Story;
- Server Sync.

Главное правило:

> **Игровая экипировка усиливает RPG-прогрессию, но математическая прогрессия остаётся независимой и не может быть обойдена предметами.**
