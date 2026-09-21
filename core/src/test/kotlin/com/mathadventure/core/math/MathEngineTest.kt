package com.mathadventure.core.math

import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.TaskMode
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MathEngineTest {
    private val engine = BasicMathEngine()

    private fun task(answer: String) = TaskInstance(
        taskId = "t1",
        skillId = "ADD_BASIC",
        mode = TaskMode.DIRECT,
        difficulty = 1,
        contextType = "TEST",
        prompt = "2 + 3 = ?",
        inputType = InputType.NUMERIC,
        answerSpec = answer
    )

    @Test
    fun evaluatesCorrectAndIncorrectAnswers() {
        assertTrue(engine.evaluateAnswer(task("5"), "5").result.name == "CORRECT")
        assertFalse(engine.evaluateAnswer(task("5"), "4").result.name == "CORRECT")
    }

    @Test
    fun validatesTaskBeforeAnswerEvaluation() {
        assertFalse(engine.validateTask(task("")).valid)
        assertTrue(engine.validateTask(task("5")).valid)
    }
}
