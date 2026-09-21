# Math Adventure — Math Task Pipeline Contract

**Version:** 1.0  
**Status:** APPROVED ARCHITECTURE BASELINE  
**Date:** 2026-09-21

## 1. Purpose

Define the contract from adaptive decision to playable validated mathematical task.

## 2. Pipeline

`Adaptive Decision → Task Blueprint → Task Generator → Task Instance → Structural Validation → Logical Validation → Math Engine Validation → Validated Task → Game Engine`

## 3. Adaptive Decision

Minimum conceptual output:

- `skillId`
- `mode`
- `difficulty`
- `contextType`
- task constraints
- pedagogical reason/selection metadata where required for diagnostics

Adaptive does not produce the concrete question text or final answer.

## 4. Task Blueprint

The Blueprint is the generator-facing contract. It constrains:

- skill;
- mode;
- difficulty;
- input type;
- numeric ranges/structural constraints;
- required/forbidden patterns;
- context;
- answer representation;
- hint metadata where applicable.

The Blueprint must be sufficient to generate a concrete task without giving Task Generator ownership of pedagogical selection.

## 5. Task Instance

A Task Instance contains the concrete learner-facing task plus machine-verifiable answer data and provenance.

Conceptual fields:

- `taskId`
- `skillId`
- `mode`
- `difficulty`
- `contextType`
- `prompt`
- `inputType`
- `answerSpec`
- `hintSpec`
- `generationMetadata`

## 6. Validation

### Structural validation
Checks schema, required fields, supported input type, ranges and representation.

### Logical validation
Checks internal consistency: answer representation, options, constraints, uniqueness and task logic.

### Mathematical validation
Math Engine verifies the mathematical truth and expected answer.

Only a task that passes all three layers may enter gameplay.

## 7. Attempt result

The learner submits an answer through the UI according to the Task Instance input type.

Math Engine evaluates mathematical correctness and produces validated attempt evidence.

Mastery System consumes that evidence and owns the Mastery update.

Game Engine consumes the gameplay-relevant result; Economy applies rewards according to game rules.

## 8. Explicit non-responsibilities

- Task Generator does not update Mastery.
- Task Generator does not award Coins.
- Math Engine does not choose the next pedagogical step.
- Game Engine does not reinterpret mathematical correctness.
