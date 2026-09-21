package com.mathadventure.core.math

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.TaskInstance

data class MathematicalValidation(
    val valid: Boolean,
    val reason: String? = null
)

data class AttemptEvaluation(
    val result: AnswerResult,
    val evidence: Map<String, String> = emptyMap()
)

interface MathEngine {
    fun validateTask(task: TaskInstance): MathematicalValidation
    fun evaluateAnswer(task: TaskInstance, submittedAnswer: String): AttemptEvaluation
}

class BasicMathEngine : MathEngine {
    override fun validateTask(task: TaskInstance): MathematicalValidation {
        if (task.taskId.isBlank() || task.skillId.isBlank() || task.prompt.isBlank()) {
            return MathematicalValidation(false, "Task identity and prompt are required")
        }
        if (task.difficulty < 1) {
            return MathematicalValidation(false, "Difficulty must be positive")
        }
        return MathematicalValidation(true)
    }

    override fun evaluateAnswer(task: TaskInstance, submittedAnswer: String): AttemptEvaluation {
        val validation = validateTask(task)
        require(validation.valid) { "Cannot evaluate an invalid mathematical task: ${validation.reason}" }
        val correct = submittedAnswer.trim() == task.answerSpec.trim()
        return AttemptEvaluation(
            result = if (correct) AnswerResult.CORRECT else AnswerResult.INCORRECT,
            evidence = mapOf(
                "skillId" to task.skillId,
                "difficulty" to task.difficulty.toString(),
                "contextType" to task.contextType
            )
        )
    }
}
