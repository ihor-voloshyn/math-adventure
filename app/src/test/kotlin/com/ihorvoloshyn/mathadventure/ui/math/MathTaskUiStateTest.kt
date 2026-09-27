package com.ihorvoloshyn.mathadventure.ui.math

import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.TaskMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MathTaskUiStateTest {
    private val task = TaskInstance(
        taskId = "task-1", skillId = "ADD_BASIC", mode = TaskMode.DIRECT, difficulty = 1,
        contextType = "BATTLE", prompt = "7 + 5 = ?", inputType = InputType.SELECTION,
        answerSpec = "12", hintSpec = "COUNT ON FROM 7"
    )
    private val machine = MathTaskStateMachine()

    @Test
    fun presentStartsInIdle() {
        val state = machine.present(task, 2, 5)
        assertTrue(state is MathTaskUiState.Idle)
        assertEquals(2, state.taskIndex)
        assertEquals(5, state.totalTasks)
    }

    @Test
    fun selectingAnswerDoesNotEvaluateIt() {
        val state = machine.select(machine.present(task, 2, 5), "12")
        assertEquals("12", state.selectedAnswer)
    }

    @Test
    fun correctEvaluationProducesCorrectState() {
        val selected = machine.select(machine.present(task, 2, 5), "12")
        val state = machine.resolve(selected, AttemptEvaluation(AnswerResult.CORRECT))
        val correct = assertIs<MathTaskUiState.Correct>(state)
        assertEquals("12", correct.selectedAnswer)
    }

    @Test
    fun incorrectEvaluationProducesIncorrectState() {
        val selected = machine.select(machine.present(task, 2, 5), "11")
        val state = machine.resolve(selected, AttemptEvaluation(AnswerResult.INCORRECT))
        val incorrect = assertIs<MathTaskUiState.Incorrect>(state)
        assertEquals("11", incorrect.selectedAnswer)
    }

    @Test
    fun skippedEvaluationProducesSkippedState() {
        val selected = machine.select(machine.present(task, 2, 5), "12")
        val state = machine.resolve(selected, AttemptEvaluation(AnswerResult.SKIPPED))
        val skipped = assertIs<MathTaskUiState.Skipped>(state)
        assertEquals(AnswerResult.SKIPPED, skipped.evaluation.result)
    }
}
