package com.mathadventure.core.math

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.TaskInstance

class BasicMathEngine : MathEngine {
    override fun validateTask(task: TaskInstance): MathematicalValidation {
        if (task.taskId.isBlank()) return MathematicalValidation(false, "MISSING_TASK_ID")
        if (task.skillId.isBlank()) return MathematicalValidation(false, "MISSING_SKILL_ID")
        if (task.prompt.isBlank()) return MathematicalValidation(false, "MISSING_PROMPT")
        if (task.answerSpec.isBlank()) return MathematicalValidation(false, "MISSING_ANSWER_SPEC")
        if (task.difficulty < 1) return MathematicalValidation(false, "INVALID_DIFFICULTY")
        return MathematicalValidation(true)
    }

    override fun evaluateAnswer(task: TaskInstance, submittedAnswer: String): AttemptEvaluation {
        require(validateTask(task).valid) { "Task must be mathematically valid before answer evaluation" }
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
