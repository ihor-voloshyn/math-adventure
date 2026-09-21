package com.mathadventure.core.contracts

import com.mathadventure.core.model.*

interface MasterySystem {
    fun getSkillState(playerId: String, skillId: String): SkillState
    fun applyValidatedAttempt(input: MasteryUpdateInput): SkillState
}

interface AdaptiveEngine {
    fun decideNext(
        playerId: String,
        skillStates: List<SkillState>,
        availableSkills: Set<String>
    ): AdaptiveDecision
}

interface TaskGenerator {
    fun generate(blueprint: TaskBlueprint): TaskInstance
}

interface TaskValidator {
    fun validate(task: TaskInstance): ValidationResult
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val layer: ValidationLayer, val reason: String) : ValidationResult
}

enum class ValidationLayer {
    STRUCTURAL,
    LOGICAL,
    MATHEMATICAL
}
