# 12_MASTERY_PROGRESSION.md

**Проект:** Math Adventure  
**Документ:** Mastery Progression Algorithm  
**Версия:** 1.0  
**Статус:** APPROVED  
**Дата:** 2026-09-22

## 1. Назначение

Документ определяет MVP-алгоритм изменения Mastery конкретного математического Skill.

Главная цель:

> Mastery должен отражать устойчивое математическое понимание, а не количество правильных ответов подряд.

## 2. Шкала

Mastery использует значения 0–5:

| Mastery | Состояние |
|---:|---|
| 0 | Unknown / Not Learned |
| 1 | Introduction |
| 2 | Understanding |
| 3 | Working Proficiency |
| 4 | Stable Proficiency |
| 5 | Mastery |

## 3. Evidence

Каждая валидированная попытка является evidence.

Учитываются correctness, skill, difficulty, task mode, context, hint usage, timestamp и recent history.

Mastery System не должен хранить только aggregate accuracy.

## 4. Основной принцип обновления

MVP использует evidence-based state machine с ограничением скорости изменения.

- один правильный ответ не переводит Skill сразу в Mastery 5;
- одна ошибка не сбрасывает Mastery в 0;
- повышение происходит ступенчато;
- снижение требует более сильного evidence, чем одиночная ошибка;
- новые уровни подтверждаются различными задачами.

## 5. Условия повышения

Для перехода на следующий уровень необходимы:
1. достаточное успешное evidence;
2. отсутствие критической серии недавних ошибок;
3. допустимая сложность;
4. для 3→4 и 4→5 — разнообразие evidence;
5. для Mastery 5 — delayed review.

Точные пороги задаются конфигурацией policy.

## 6. Рекомендуемая MVP policy

- 0→1: первая успешная демонстрация Skill;
- 1→2: устойчивые успешные попытки на вводной сложности;
- 2→3: несколько успешных попыток с обычной сложностью;
- 3→4: успешные попытки на разных задачах/представлениях и хотя бы в двух допустимых контекстах;
- 4→5: стабильное выполнение на подходящей сложности + разнообразие + delayed review.

Это policy defaults, которые могут быть откалиброваны после QA и реальных данных.

## 7. Diversity

Для Mastery 4 и 5 нельзя использовать только повтор одной формы.

Нужны вариации:
- direct expression;
- word problem;
- unknown component;
- inverse;
- representation;
- разные числа;
- разные игровые контексты.

Только те варианты, которые поддерживает конкретный Skill.

## 8. Context Transfer

Для устойчивого Mastery система должна проверять перенос навыка.

Например ADD_BASIC может быть проверен в Battle, Shop, Quest и Puzzle.

Не каждый Skill обязан пройти все контексты.

## 9. Difficulty Evidence

Успех на минимальной сложности не является достаточным основанием для Mastery 5.

При повышении Mastery система постепенно получает evidence на сложности, соответствующей целевому уровню Skill.

## 10. Ошибки

Одиночная ошибка:
- не обнуляет Mastery;
- не считается автоматической Remediation;
- фиксируется как negative evidence.

Серия ошибок:
- снижает confidence;
- может инициировать Review;
- при устойчивом повторении может снизить Mastery на один уровень;
- может передать Adaptive Engine сигнал для Remediation.

## 11. Controlled Demotion

MVP не использует резкое падение 5→0.

Обычное снижение происходит ступенчато и требует подтверждённого отрицательного evidence.

Это защищает ребёнка от ощущения, что один неудачный ответ «забрал» изученное знание.

## 12. Review

После Mastery 4 или 5 Skill становится кандидатом для периодического Review.

Review использует новую задачу, по возможности меняет представление и учитывает время с последней проверки.

## 13. Delayed Review для Mastery 5

Mastery 5 нельзя получить только внутри одной короткой серии.

Нужна успешная delayed verification после паузы или достаточного количества других математических задач.

Если delayed review успешен — Mastery 5 подтверждается.

Если нет — Skill возвращается в Review/4 с последующим наблюдением.

## 14. SKIPPED

SKIPPED:
- не CORRECT;
- не INCORRECT;
- не повышает Mastery;
- не снижает Mastery;
- сохраняется как нейтральное evidence.

## 15. Hints

Использование математической подсказки не является ошибкой.

CORRECT + HINT:
- считается успешной попыткой;
- hintUsed сохраняется;
- может дать более слабое evidence для повышения высокого Mastery, чем CORRECT без подсказки.

Подсказка не уменьшает Mastery напрямую.

## 16. Скорость

Время ответа не является обязательным критерием Mastery.

Медленный правильный ответ может быть полноценным evidence.

Speed может храниться отдельно для аналитики.

## 17. Ранние уровни

На Mastery 0–2 система должна быть дружелюбной:
- простые задачи;
- понятные представления;
- подсказки;
- небольшие шаги сложности.

## 18. Высокие уровни

На Mastery 3–5 система больше проверяет:
- вариативность;
- перенос;
- самостоятельность;
- подходящую сложность;
- delayed retention.

## 19. Диагностический режим

Diagnostic evidence может быстро определить ориентировочный уровень, но не должен автоматически превращать Skill в окончательный Mastery 5.

Высокий результат диагностики требует подтверждения обычными игровыми задачами и/или delayed review.

## 20. Новый Skill

Для нового ребёнка Mastery = UNKNOWN.

После первой успешной демонстрации Skill состояние может перейти к 1.

После устойчивого evidence оно постепенно повышается.

## 21. Забывание

MVP не использует агрессивное автоматическое снижение Mastery только из-за времени.

Время используется прежде всего для Review urgency.

Mastery снижается главным образом на основании нового negative evidence.

## 22. Negative Evidence

Для снижения учитываются количество ошибок, близость ошибок по времени, сложность, context/mode, предыдущая стабильность, наличие подсказки и успешные ответы после ошибки.

Последующие успехи могут нейтрализовать сигнал одной ошибки.

## 23. Confidence

Внутреннее состояние может использовать confidence/evidence strength, но UI ребёнка не обязан показывать его.

Mastery — пользовательски значимая шкала 0–5.

## 24. Policy Configuration

Mastery Policy должна быть конфигурационной:
- promotion evidence;
- demotion evidence;
- review intervals;
- diversity requirements;
- difficulty requirements;
- hint weighting;
- diagnostic confirmation.

## 25. Запрещённые упрощения

Нельзя использовать:
- Mastery = correct / attempts;
- «5 correct in a row = Mastery 5»;
- speed-only;
- age-based Mastery.

## 26. Связь с Adaptive Engine

Mastery System публикует current Mastery, recent evidence, review status, confidence/evidence strength и relevant error signals.

Adaptive Engine использует эти данные для выбора следующего шага.

Mastery System не выбирает следующий Skill.

## 27. Связь с Math Engine

Math Engine определяет математическую правильность.

Mastery System получает уже валидированную попытку:

Validated Math Attempt → Mastery Update

Mastery не перепроверяет арифметику самостоятельно.

## 28. Связь с Task Generator

Task Generator создаёт конкретную задачу.

Mastery System не генерирует задания.

## 29. Связь с Game Engine

Mastery System не управляет XP, Coins, Loot, Inventory, Combat или Quests.

## 30. Persistence

Mastery state и evidence должны сохраняться атомарно относительно подтверждённой математической попытки.

Повторная обработка одного attemptId не должна применить Mastery Update дважды.

## 31. Offline-first

Mastery обновляется локально без постоянного подключения.

Будущая синхронизация передаёт evidence/events на сервер для валидации.

## 32. Telemetry

Рекомендуемые поля:
- attemptId;
- skillId;
- masteryBefore;
- masteryAfter;
- evidenceStrength;
- promotion/demotion reason;
- policyVersion;
- timestamp.

## 33. Acceptance Criteria

1. Одна ошибка не сбрасывает Mastery.
2. Один правильный ответ не даёт Mastery 5.
3. Mastery 4/5 требует разнообразного evidence.
4. Mastery 5 требует delayed verification.
5. SKIPPED нейтрален.
6. Hints не являются ошибкой.
7. Speed не является основным критерием.
8. Mastery не вычисляется как accuracy.
9. RPG Level не изменяет Mastery.
10. Game Engine не изменяет Mastery.
11. Повторная обработка attemptId идемпотентна.
12. Mastery работает offline.
13. Policy можно менять без изменения Game Engine.

## 34. Статус

**Mastery Progression Algorithm 1.0 — APPROVED**

Следующий блок: **Game Engine Progression Boundary** — события игры, XP/Coins/Loot, атомарность и связь с RPG Level.
