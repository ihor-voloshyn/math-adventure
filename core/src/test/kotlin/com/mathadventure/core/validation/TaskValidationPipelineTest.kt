package com.mathadventure.core.validation

import com.mathadventure.core.contracts.TaskValidator
import com.mathadventure.core.contracts.ValidationLayer
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.TaskMode
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskValidationPipelineTest {
    private val task = TaskInstance(
        taskId = "t1",
        skillId = "addition",
        mode = TaskMode.DIRECT,
        difficulty = 1,
        contextType = "diagnostic",
        prompt = "2 + 3 = ?",
        inputType = InputType.NUMERIC,
        answerSpec = "5"
    )

    @Test
    fun pipelineStopsAtFirstFailure() {
        var mathematicalCalled = false
        val validator = object : TaskValidator {
            override fun validate(task: TaskInstance): ValidationResult =
                ValidationResult.Valid
        }
        val logical = object : TaskValidator {
            override fun validate(task: TaskInstance): ValidationResult =
                ValidationResult.Invalid(ValidationLayer.LOGICAL, "invalid options")
        }
        val mathematical = object : TaskValidator {
            override fun validate(task: TaskInstance): ValidationResult {
                mathematicalCalled = true
                return ValidationResult.Valid
            }
        }

        val result = TaskValidationPipeline(validator, logical, mathematical).validate(task)

        assertEquals(
            ValidationResult.Invalid(ValidationLayer.LOGICAL, "invalid options"),
            result
        )
        assertEquals(false, mathematicalCalled)
    }
}
