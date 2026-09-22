# 19_WORLD_STORY.md

**Проект:** Math Adventure  
**Документ:** World & Story — мир и сюжет  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

## 1. Назначение

World & Story определяет первый игровой мир, его структуру и сюжетную арку.

Цель — дать ребёнку ощущение настоящего приключения, в котором математика является естественным инструментом действий героя.

## 2. Первый мир

MVP first world включает:
- Home/Territory;
- Village;
- Forest;
- small Dungeon;
- surrounding paths;
- first Dragon story thread.

Это компактный connected adventure world, а не огромный open world.

## 3. World Structure

```
HOME
  ↓
VILLAGE
  ↓
FOREST
  ↓
DUNGEON
  ↓
DEEPER WORLD
```

Доступность частей мира определяется Game Progression/World Unlocks и не должна заменять математические prerequisites.

## 4. Home

Home:
- безопасная зона;
- личное пространство;
- визуальный progression anchor;
- место питомца;
- место оборудования;
- story return point.

## 5. Village

Village — первый социальный hub.

Содержит:
- central plaza;
- shop;
- quest NPCs;
- library/knowledge location;
- paths to world zones;
- small number of houses;
- visible world landmarks.

Village должен ощущаться живым без необходимости сложной NPC simulation.

## 6. Forest

Forest — первая большая exploration zone.

Содержит:
- paths;
- landmarks;
- enemies;
- discoveries;
- quest objectives;
- hidden/optional locations;
- route to dungeon.

## 7. Dungeon

Dungeon — следующая major zone.

MVP может начинаться с небольшой подземной зоны:
- entrance;
- several rooms;
- enemies;
- treasure;
- story object;
- first stronger encounter.

## 8. Dragon

Dragon является долгосрочным story antagonist.

На ранней стадии ребёнок не должен сразу получить полную историю дракона.

Сначала:
- слухи;
- следы;
- последствия;
- NPC stories;
- distant visual hints.

Позднее:
- встречи;
- конфликт;
- раскрытие мотивации;
- крупные story milestones.

## 9. Story Tone

Тон:
- adventure;
- mystery;
- friendship;
- discovery;
- light fantasy danger.

Не использовать:
- horror;
- graphic violence;
- grimdark;
- сложные политические темы.

## 10. Story Arc

Первая большая арка:

1. Новый герой получает дом.
2. Герой знакомится с питомцем.
3. Герой знакомится с деревней.
4. Возникает небольшая проблема.
5. Герой выполняет первые задания.
6. Герой впервые исследует лес.
7. Появляются первые признаки дракона.
8. Герой обнаруживает dungeon.
9. Герой узнаёт, что происходящее связано с более крупной историей.
10. Арка приводит к RPG Level 30 / Castle milestone.

Level 30 завершает первую крупную арку, но не всю игру.

## 11. Story Delivery

История должна подаваться через:
- короткие диалоги;
- NPC;
- визуальные события;
- предметы;
- environmental storytelling;
- quest objectives;
- world changes.

Не требуется длинных cutscenes.

## 12. Child-first Narrative

Диалоги:
- короткие;
- простые;
- эмоционально понятные;
- без длинных монологов.

Основное правило:

> show more, explain less.

## 13. Player Agency

Ребёнок должен:
- выбирать, куда идти в доступной зоне;
- исследовать;
- выбирать допустимое действие в бою;
- возвращаться домой;
- решать, какие доступные активности выполнять.

Но MVP не требует сложной branching narrative.

## 14. RPG Level Integration

RPG milestones открывают новые игровые возможности.

Примеры:
- Level 5: расширение Home/первые новые adventure opportunities;
- Level 10: полноценная Forest exploration;
- Level 15: новая зона/quest type;
- Level 20: Dungeon;
- Level 25: крупная story/territory stage;
- Level 30: Castle + завершение первой большой story arc.

Эти unlocks не заменяют Mastery gates.

## 15. Mathematical Integration

Математика появляется в естественных действиях:
- attack;
- puzzle;
- shop;
- quest;
- exploration;
- construction;
- discovery.

World/Story не определяет математический Skill.

## 16. World Events

WorldEvent:
- id;
- type;
- sourceId;
- timestamp;
- state;
- metadata.

Примеры:
- AREA_UNLOCKED;
- STORY_MILESTONE;
- NPC_INTRODUCED;
- DUNGEON_OPENED;
- DRAGON_CLUE_FOUND;
- HOME_CHANGED.

Events должны быть idempotent для critical progression.

## 17. World State

Минимально:
- unlockedAreas;
- activeStoryStage;
- introducedNpcs;
- discoveredLandmarks;
- worldObjectsState;
- completedStoryEvents.

World state не хранит Mastery.

## 18. Persistence

World state сохраняется локально и переживает:
- restart;
- load;
- defeat;
- offline sessions.

## 19. Defeat

Поражение не сбрасывает:
- opened areas;
- discovered landmarks;
- story milestones;
- home;
- pet;
- confirmed equipment;
- confirmed progression.

## 20. Offline-first

World/Story должен работать офлайн.

Синхронизация в будущем передаёт authoritative progression events и state.

## 21. Visual Progression

Мир должен visibly change:
- новые здания;
- открытые дороги;
- NPC;
- объекты;
- Home upgrades;
- new zone entrances.

Это должно быть заметнее технического индикатора RPG Level.

## 22. First Playable World Slice

Первый playable должен быть маленьким:

```
HOME
  ↓
short path
  ↓
VILLAGE PLAZA
  ↓
one NPC
  ↓
small exploration area
  ↓
one enemy encounter
  ↓
one Math Task
  ↓
return HOME
```

Это достаточно для первого child test.

## 23. Vertical Slice

После первого playable:
- expanded village;
- forest;
- several NPCs;
- multiple enemies;
- quest chain;
- Home progression;
- pet progression;
- equipment;
- first dungeon entrance;
- first Dragon clues.

## 24. Asset Production

Asset production follows the approved Visual Style/Asset Specification.

Не производить полный мир заранее.

Сначала playable slice → child test → refine → expand.

## 25. QA

Минимально:
- area unlock;
- world state persistence;
- story event idempotency;
- NPC introduction;
- discovery;
- Home integration;
- Quest integration;
- Combat integration;
- defeat preservation;
- offline;
- sync recovery;
- visual state after load.

## 26. Acceptance Criteria

World/Story готов для MVP, если:
1. есть Home, Village, Forest и small Dungeon;
2. есть понятная первая story arc;
3. Dragon thread присутствует;
4. ребёнок понимает куда идти и зачем;
5. world changes visibly with progression;
6. story state persists;
7. defeat does not erase confirmed progress;
8. world works offline;
9. World/Story does not own Mastery;
10. first playable slice can be completed in a short session.

## 27. Основной принцип

> Мир должен давать ребёнку причину продолжать.

> История должна создавать любопытство.

> Математика должна быть естественной частью действий героя.

> Визуальный прогресс должен быть виден непосредственно в мире.

## 28. Статус

19_WORLD_STORY.md v1.0 — APPROVED.
