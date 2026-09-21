package com.mathadventure.core.validation

import com.mathadventure.core.contracts.TaskValidator
import com.mathadventure.core.contracts.ValidationLayer
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.model.TaskInstance

class TaskValidationPipeline(
    private val structural: TaskValidator,
    private val logical: TaskValidator,
    private val mathematical: TaskValidator
) {
    fun validate(task: TaskInstance): ValidationResult {
        val structuralResult = structural.validate(task)
        if (structuralResult is ValidationResult.Invalid) {
            return structuralResult.copy(layer = ValidationLayer.STRUCTURAL)
        }

        val logicalResult = logical.validate(task)
        if (logicalResult is ValidationResult.Invalid) {
            return logicalResult.copy(layer = ValidationLayer.LOGICAL)
        }

        val mathematicalResult = mathematical.validate(task)
        if (mathematicalResult is ValidationResult.Invalid) {
            return mathematicalResult.copy(layer = ValidationLayer.MATHEMATICAL)
        }

        return ValidationResult.Valid
    }
}
