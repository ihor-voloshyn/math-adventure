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
