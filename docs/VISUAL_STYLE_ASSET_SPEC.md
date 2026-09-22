# VISUAL_STYLE_ASSET_SPEC.md

**Проект:** Math Adventure  
**Документ:** Visual Style & Asset Specification — визуальный стиль и минимальный набор ассетов  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

## 1. Назначение

Документ определяет визуальное направление и минимальный набор 3D-ассетов, необходимый для первого Playable Prototype и последующего Vertical Slice.

Цель — не создать весь мир заранее, а как можно раньше получить визуально цельный фрагмент игры, который можно показать ребёнку.

## 2. Визуальная цель

Math Adventure должна восприниматься как:

- дружелюбное 3D fantasy adventure;
- яркий, но не перегруженный мир;
- персонажи с выразительными силуэтами;
- мягкая стилизация вместо реалистичной мрачности;
- визуально понятные интерактивные объекты;
- достаточно «приключенческая» атмосфера для 8-летнего ребёнка;
- единый стиль героя, питомца, NPC, врагов, предметов и окружения.

Визуальный стиль должен быть child-first, но не выглядеть как игра для дошкольников.

## 3. Базовое направление

Рекомендуемое направление:

**Stylized 3D Fantasy Adventure**

Характеристики:
- выразительные пропорции;
- умеренно крупные головы и читаемые лица;
- чистые формы;
- простые материалы;
- умеренная детализация;
- сильные силуэты;
- тёплые безопасные цвета для дружелюбных зон;
- более контрастные цвета для опасных зон;
- отсутствие реалистичного gore.

## 4. Герой

Первый Hero Asset должен иметь:

- нейтральную базовую модель;
- читаемый силуэт;
- возможность смены одежды/брони;
- видимые helmet/armor/weapon;
- совместимость с камерой от третьего лица;
- базовые анимации.

Минимальный animation set:
- idle;
- walk;
- run;
- interact;
- attack;
- defend;
- hurt;
- victory;
- defeat.

Пол не выбирается отдельным обязательным шагом. Внешний вид определяется базовым персонажем и экипировкой.

## 5. Первый визуальный Hero

Для первого Prototype достаточно одной основной модели героя.

Не требуется сразу:
- много лиц;
- десятки причёсок;
- сложный creator;
- полный гардероб.

Сначала проверяется:
- силуэт;
- читаемость;
- движение;
- взаимодействие с миром;
- визуальная привлекательность.

## 6. Питомец

Обязательные варианты:
- kitten;
- puppy.

Для Prototype достаточно одного из них, но архитектура должна сразу поддерживать оба.

Минимальная стадия:
- Baby.

Для Vertical Slice:
- Baby;
- Young;
- Explorer Companion.

Анимации:
- idle;
- walk/run;
- follow;
- interact;
- happy;
- rest.

## 7. NPC

Первый NPC:
- дружелюбный;
- хорошо отличимый от героя;
- с простым силуэтом;
- с базовыми idle/walk/talk animations.

Не требуется создавать весь набор NPC до Prototype.

## 8. Первый враг

Первый враг должен быть легко читаемым и не страшным.

Рекомендуемый archetype:
- small fantasy creature / goblin-like enemy.

Минимальные анимации:
- idle;
- move;
- attack;
- hurt;
- defeat.

## 9. Дом

Первый Home Asset должен визуально поддерживать:
- маленький дом;
- дверь;
- окна;
- кровлю;
- место питомца;
- двор;
- простой декоративный объект.

Home должен иметь заметные upgrade states.

Минимум:
- HOME_01;
- HOME_05.

Vertical Slice:
- HOME_10;
- HOME_15.

Позднее:
- HOME_20;
- HOME_25;
- HOME_30.

## 10. Территория

Минимальная сцена:
- небольшой участок;
- трава/земля;
- дорожка;
- несколько деревьев/кустов;
- камни;
- простой забор;
- выход в мир.

Не требуется создавать большую open-world карту.

## 11. Деревня

Vertical Slice должен содержать:
- площадь;
- несколько домов;
- магазин;
- quest hub;
- NPC;
- путь к лесу.

Для Prototype достаточно визуально малого участка деревни.

## 12. Лес

Для Vertical Slice:
- деревья;
- кусты;
- камни;
- дорожки;
- интерактивные точки;
- небольшой опасный участок;
- первый enemy encounter.

## 13. Подземелье

Не входит в самый первый Prototype.

Для более позднего Vertical Slice:
- вход;
- несколько комнат;
- простые стены;
- освещение;
- первый dungeon enemy set.

## 14. Оружие и броня

Минимальный набор:
- sword;
- staff;
- simple armor;
- helmet;
- accessory.

Все предметы должны иметь стабильные Asset IDs и соответствовать Items & Equipment.

Оборудованный предмет должен одинаково идентифицироваться в:
- доме;
- бою;
- инвентаре;
- character screen.

## 15. Предметы окружения

Минимально:
- chest;
- table;
- chair;
- bed;
- pet bed;
- shelves;
- lamp;
- sign;
- fence;
- rocks;
- trees;
- bushes.

## 16. UI

Prototype UI:
- health/hearts;
- math task panel;
- answer input;
- basic quest marker;
- interaction prompt;
- RPG Level/XP indicator;
- simple inventory/equipment view.

UI должен оставаться простым и не превращать игровой экран в dashboard.

## 17. VFX

Минимально:
- attack effect;
- hit effect;
- success feedback;
- error feedback;
- level-up effect;
- unlock effect;
- small magic effect.

Не требуется сложный VFX pipeline до Vertical Slice.

## 18. Camera

Камера:
- third-person;
- rotatable;
- плавное следование;
- ограничение столкновений;
- читаемость героя и ближайшего интерактивного объекта.

Camera должен позволять ребёнку видеть:
- героя;
- питомца;
- ближайшего NPC;
- математическую интеракцию;
- окружение.

## 19. Lighting

Визуальная система должна поддерживать день/ночь, но Prototype может использовать ограниченный дневной режим.

Полная day/night gameplay system реализуется позднее в World layer.

## 20. Asset IDs

Примеры:
- hero_default;
- pet_kitten_baby;
- pet_puppy_baby;
- npc_shopkeeper_01;
- enemy_goblin_01;
- home_stage_01;
- home_stage_05;
- territory_stage_01;
- weapon_sword_common_01;
- weapon_staff_common_01;
- armor_basic_01;
- helmet_basic_01.

IDs не зависят от языка.

## 21. Asset Pipeline

Каждый production asset должен иметь:
- assetId;
- category;
- version;
- source;
- license/ownership information;
- target platform;
- LOD policy;
- collision policy;
- animation dependencies;
- thumbnail/reference.

## 22. Prototype Asset Budget

Первый Playable Prototype должен ограничиться примерно:
- 1 Hero;
- 1 Pet;
- 1 NPC;
- 1 Enemy;
- 1 Home;
- 1 small Territory;
- 1 village fragment;
- 3–5 equipment items;
- 10–20 environment props;
- basic UI;
- basic VFX.

Это не окончательный контент MVP, а минимальный визуальный proof-of-concept.

## 23. Vertical Slice Asset Budget

После успешного Prototype:
- 1 polished Hero;
- 2 Pets;
- 3–5 NPC archetypes;
- 3–5 enemy archetypes;
- Home stages 1/5/10/15;
- expanded Territory;
- village;
- forest;
- dungeon entrance;
- 15–30 equipment/item assets;
- expanded prop library;
- core VFX;
- polished UI.

## 24. Quality Bar

Asset принимается в Prototype, если:
1. silhouette читается на мобильном экране;
2. модель не конфликтует с камерой;
3. animation transitions не ломаются;
4. collision работает;
5. material/lighting выглядит единообразно;
6. asset имеет стабильный ID;
7. asset не создаёт технических проблем для target Android devices.

## 25. Child Testing

Первый визуальный Prototype должен быть проверен ребёнком до расширения ассетного производства.

Проверяем:
- хочет ли ребёнок управлять героем;
- нравится ли питомец;
- понятен ли дом;
- различает ли NPC и врага;
- привлекает ли мир;
- понятна ли математическая интеракция;
- хочется ли продолжить после первой задачи.

## 26. Производственный порядок

1. Visual mood/style references.
2. Hero concept.
3. Hero 3D prototype.
4. Pet prototype.
5. Home prototype.
6. NPC prototype.
7. Enemy prototype.
8. Small Territory.
9. First gameplay scene.
10. First Playable build.
11. Child test.
12. Style refinement.
13. Vertical Slice asset expansion.
14. MVP asset production.

## 27. Не делать до первого теста

Не производить заранее:
- десятки NPC;
- полный набор врагов;
- все 30 уровней дома;
- полный замок;
- сотни предметов;
- полный набор VFX;
- огромную карту.

Сначала проверяется core visual identity.

## 28. Архитектурная связь

Visual Assets are consumed by Game Engine and presentation layers.

Math Engine не зависит от конкретных моделей.

Adaptive Engine не зависит от конкретных моделей.

Task Generator может ссылаться на abstract context/representation IDs, но не на физические scene coordinates.

## 29. MVP принцип

Визуальная разработка не откладывается до конца документации.

Первый настоящий playable должен появиться после создания минимального visual asset slice.

Главный критерий:

**ребёнок должен увидеть игру, а не набор технических систем.**

## 30. Статус

VISUAL_STYLE_ASSET_SPEC.md v1.0 — APPROVED.
