package com.mathadventure.core.validation

import com.mathadventure.core.contracts.TaskValidator
import com.mathadventure.core.contracts.ValidationLayer
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance

class StructuralTaskValidator : TaskValidator {
    override fun validate(task: TaskInstance): ValidationResult {
        if (task.taskId.isBlank()) return invalid("MISSING_TASK_ID")
        if (task.skillId.isBlank()) return invalid("MISSING_SKILL_ID")
        if (task.prompt.isBlank()) return invalid("MISSING_PROMPT")
        if (task.answerSpec.isBlank()) return invalid("MISSING_ANSWER_SPEC")
        if (task.difficulty < 1) return invalid("INVALID_DIFFICULTY")
        return ValidationResult.Valid
    }
    private fun invalid(reason: String) = ValidationResult.Invalid(ValidationLayer.STRUCTURAL, reason)
}
class LogicalTaskValidator : TaskValidator {
    override fun validate(task: TaskInstance): ValidationResult {
        if (task.inputType == InputType.SELECTION && task.answerSpec == "UNSUPPORTED")
            return ValidationResult.Invalid(ValidationLayer.LOGICAL, "INVALID_SELECTION_ANSWER")
        if (task.prompt.contains("?") && task.answerSpec == "UNSUPPORTED")
            return ValidationResult.Invalid(ValidationLayer.LOGICAL, "UNSUPPORTED_TASK_STRUCTURE")
        return ValidationResult.Valid
    }
}
