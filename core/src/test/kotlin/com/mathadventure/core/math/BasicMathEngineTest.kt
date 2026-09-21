package com.mathadventure.core.math

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.TaskMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BasicMathEngineTest {
    private val engine = BasicMathEngine()

    @Test
    fun validatesCorrectAddition() {
        val task = task("12 + 5 = ?", "17")
        assertTrue(engine.validateTask(task).valid)
    }

    @Test
    fun rejectsIncorrectExpectedAnswer() {
        val result = engine.validateTask(task("12 + 5 = ?", "18"))
        assertFalse(result.valid)
        assertEquals("INCORRECT_EXPECTED_ANSWER", result.reason)
    }

    @Test
    fun evaluatesCorrectAndIncorrectAnswers() {
        val task = task("12 + 5 = ?", "17")
        assertEquals(AnswerResult.CORRECT, engine.evaluateAnswer(task, "17").result)
        assertEquals(AnswerResult.INCORRECT, engine.evaluateAnswer(task, "18").result)
    }

    private fun task(prompt: String, answer: String) = TaskInstance(
        "task-test",
        "ADD_BASIC",
        TaskMode.DIRECT,
        1,
        "TEST",
        prompt,
        InputType.NUMERIC,
        answer
    )
}
