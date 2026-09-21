package com.mathadventure.core.math

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.TaskInstance

class BasicMathEngine : MathEngine {
    override fun validateTask(task: TaskInstance): MathematicalValidation {
        if (task.taskId.isBlank()) return invalid("MISSING_TASK_ID")
        if (task.skillId.isBlank()) return invalid("MISSING_SKILL_ID")
        if (task.prompt.isBlank()) return invalid("MISSING_PROMPT")
        if (task.answerSpec.isBlank()) return invalid("MISSING_ANSWER_SPEC")
        if (task.difficulty < 1) return invalid("INVALID_DIFFICULTY")

        val addition = ADDITION_PATTERN.matchEntire(task.prompt.trim())
            ?: return invalid("UNSUPPORTED_MATHEMATICAL_FORM")
        val left = addition.groupValues[1].toLongOrNull()
            ?: return invalid("INVALID_LEFT_OPERAND")
        val right = addition.groupValues[2].toLongOrNull()
            ?: return invalid("INVALID_RIGHT_OPERAND")
        val expected = (left + right).toString()

        if (task.answerSpec.trim() != expected) {
            return invalid("INCORRECT_EXPECTED_ANSWER")
        }
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

    private fun invalid(reason: String) = MathematicalValidation(false, reason)

    private companion object {
        val ADDITION_PATTERN = Regex("""(-?\d+)\s*\+\s*(-?\d+)\s*=\s*\?""")
    }
}
