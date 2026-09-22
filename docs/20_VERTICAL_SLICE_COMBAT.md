# 20_VERTICAL_SLICE_COMBAT.md

**Проект:** Math Adventure  
**Документ:** Vertical Slice Combat Loop  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

## 1. Назначение

Этот документ фиксирует первый реализуемый боевой цикл вертикального среза.

## 2. Цикл

```
Encounter
  ↓
Player Action
  ↓
Adaptive Math Task
  ↓
Player Answer
  ↓
Math Engine
  ↓
Combat Engine
  ↓
Enemy Turn
  ↓
Next Player Turn
```

## 3. Правила MVP

- Игрок ходит первым.
- Основные действия вертикального среза: ATTACK, DEFEND, FLEE.
- ATTACK требует математической задачи.
- CORRECT при ATTACK наносит 1 урон.
- INCORRECT при ATTACK не наносит урон и передаёт ход противнику.
- INCORRECT не означает мгновенную смерть.
- DEFEND пока не требует отдельной математической задачи и уменьшает риск хода противника в будущей реализации; текущий минимальный engine использует его как безопасное действие.
- FLEE завершает бой без победы и без XP за победу.
- Герой и враг имеют по 3 единицы боевого ресурса в базовом сценарии.
- Победа формирует событие COMBAT_VICTORY.
- Поражение не удаляет подтверждённый XP, Mastery, Coins, Loot.
- Боевой результат не изменяет Mastery напрямую.
- Combat Engine не выбирает Skill и Difficulty.
- Combat Engine не проверяет математический ответ.
- Game Progression отдельно решает XP/Coins/Loot.
- Повторное прохождение не должно создавать бесконечный XP farming: eligibility/reward policy остаются в Game Progression.

## 4. Граница

```
Adaptive → Math Task → Math Engine → Combat Engine → Game Progression
```

## 5. Следующий шаг

Подключить Combat Engine к Android vertical slice и заменить текущую заглушку «математическая атака → сразу победа» на настоящий пошаговый бой.
